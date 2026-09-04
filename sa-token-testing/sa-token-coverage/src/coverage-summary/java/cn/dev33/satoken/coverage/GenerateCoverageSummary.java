/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.coverage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 根据 JaCoCo {@code jacoco.csv} 生成带全指标百分比的 HTML 汇总页。
 *
 * <p>用法：{@code java ... GenerateCoverageSummary <csv> <html> [title] [jacocoIndexLink]}</p>
 */
public class GenerateCoverageSummary {

	private static final String[] REQUIRED_HEADERS = {
			"GROUP", "PACKAGE", "CLASS",
			"INSTRUCTION_MISSED", "INSTRUCTION_COVERED",
			"BRANCH_MISSED", "BRANCH_COVERED",
			"LINE_MISSED", "LINE_COVERED",
			"COMPLEXITY_MISSED", "COMPLEXITY_COVERED",
			"METHOD_MISSED", "METHOD_COVERED"
	};

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage: GenerateCoverageSummary <jacoco.csv> <output.html> [title] [jacocoIndexLink]");
			System.exit(1);
		}

		Path csvPath = Paths.get(args[0]);
		Path htmlPath = Paths.get(args[1]);
		String title = args.length > 2 ? args[2] : "JaCoCo Coverage Summary";
		String jacocoLink = args.length > 3 ? args[3] : "index.html";

		if (!Files.isRegularFile(csvPath)) {
			System.err.println("[JaCoCo Summary] CSV not found, skip: " + csvPath.toAbsolutePath());
			return;
		}

		Map<String, Metrics> packageMetrics = new LinkedHashMap<String, Metrics>();
		Map<String, Metrics> moduleMetrics = new LinkedHashMap<String, Metrics>();
		Metrics total = new Metrics();

		parseCsv(csvPath, packageMetrics, moduleMetrics, total);

		if (total.isEmpty()) {
			System.err.println("[JaCoCo Summary] No class rows in CSV, skip: " + csvPath);
			return;
		}

		boolean groupByModule = moduleMetrics.size() > 1;
		writeHtml(htmlPath, title, jacocoLink, total, packageMetrics, moduleMetrics, groupByModule);
		printConsoleSummary(title, htmlPath, total);
	}

	private static void parseCsv(Path csvPath, Map<String, Metrics> packageMetrics,
			Map<String, Metrics> moduleMetrics, Metrics total) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
			String headerLine = reader.readLine();
			if (headerLine == null) {
				return;
			}
			int[] index = resolveHeaderIndex(headerLine);

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				String[] cols = line.split(",", -1);
				if (cols.length < index.length) {
					continue;
				}

				String className = col(cols, index, 2);
				if (className.isEmpty()) {
					continue;
				}

				Metrics row = Metrics.fromColumns(cols, index);
				row.applyClassCoverageFromRow();

				String pkg = col(cols, index, 1);
				if (pkg.isEmpty()) {
					int dot = className.lastIndexOf('.');
					pkg = dot > 0 ? className.substring(0, dot) : "(default)";
				}

			String module = normalizeModuleName(col(cols, index, 0));

			total.add(row);
			addMetrics(packageMetrics, pkg, row);
				addMetrics(moduleMetrics, module, row);
			}
		}
	}

	private static void addMetrics(Map<String, Metrics> map, String key, Metrics row) {
		Metrics bucket = map.get(key);
		if (bucket == null) {
			bucket = new Metrics();
			map.put(key, bucket);
		}
		bucket.add(row);
	}

	/** 聚合报告 GROUP 形如 {@code sa-token-coverage/sa-token-core}，取最后一段作为模块名。 */
	private static String normalizeModuleName(String group) {
		if (group == null || group.isEmpty()) {
			return "(unknown)";
		}
		int slash = group.lastIndexOf('/');
		if (slash >= 0 && slash < group.length() - 1) {
			return group.substring(slash + 1);
		}
		return group;
	}

	private static int[] resolveHeaderIndex(String headerLine) {
		String[] headers = headerLine.split(",", -1);
		int[] index = new int[REQUIRED_HEADERS.length];
		for (int i = 0; i < REQUIRED_HEADERS.length; i++) {
			index[i] = -1;
			for (int j = 0; j < headers.length; j++) {
				if (REQUIRED_HEADERS[i].equals(headers[j].trim())) {
					index[i] = j;
					break;
				}
			}
			if (index[i] < 0 && i >= 3) {
				throw new IllegalStateException("Missing column in jacoco.csv: " + REQUIRED_HEADERS[i]);
			}
		}
		return index;
	}

	private static String col(String[] cols, int[] index, int field) {
		int i = index[field];
		return i >= 0 && i < cols.length ? cols[i].trim() : "";
	}

	private static void printConsoleSummary(String title, Path htmlPath, Metrics total) {
		System.out.println();
		System.out.println("[JaCoCo Summary] " + title);
		System.out.println("  Instructions : " + pct(total.instrMissed, total.instrCovered));
		System.out.println("  Branches     : " + pct(total.branchMissed, total.branchCovered));
		System.out.println("  Complexity   : " + pct(total.cxtyMissed, total.cxtyCovered));
		System.out.println("  Lines        : " + pct(total.lineMissed, total.lineCovered));
		System.out.println("  Methods      : " + pct(total.methodMissed, total.methodCovered));
		System.out.println("  Classes      : " + pct(total.classMissed, total.classCovered));
		System.out.println("  Report       : " + htmlPath.toAbsolutePath());
		System.out.println();
	}

	private static void writeHtml(Path htmlPath, String title, String jacocoLink,
			Metrics total, Map<String, Metrics> packageMetrics, Map<String, Metrics> moduleMetrics,
			boolean groupByModule) throws IOException {
		Path parent = htmlPath.getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}

		Map<String, Metrics> detailMetrics = groupByModule ? moduleMetrics : packageMetrics;
		String detailTitle = groupByModule ? "Modules" : "Packages";

		List<String> elements = new ArrayList<String>(detailMetrics.keySet());
		Collections.sort(elements);

		StringBuilder sb = new StringBuilder(32_768);
		sb.append("<!DOCTYPE html>\n<html lang=\"zh-CN\">\n<head>\n");
		sb.append("<meta charset=\"UTF-8\"/>\n");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>\n");
		sb.append("<title>").append(escape(title)).append("</title>\n");
		sb.append("<style>\n");
		sb.append(css());
		sb.append("</style>\n</head>\n<body>\n");
		sb.append("<h1>").append(escape(title)).append("</h1>\n");
		sb.append("<p class=\"meta\">Generated from <code>jacoco.csv</code> &middot; ");
		sb.append("<a href=\"").append(escape(jacocoLink)).append("\">JaCoCo 详细报告</a>");
		sb.append(" &middot; Classes 覆盖率由类行是否被执行推算（CSV 无独立 CLASS 列）</p>\n");

		sb.append("<h2>Total</h2>\n");
		sb.append(metricsTable(Collections.singletonList(new Row("Total", total)), true));

		sb.append("<h2>").append(detailTitle).append("</h2>\n");
		List<Row> rows = new ArrayList<Row>();
		for (String element : elements) {
			rows.add(new Row(element, detailMetrics.get(element)));
		}
		sb.append(metricsTable(rows, false));
		sb.append(sortScript());
		sb.append("</body>\n</html>\n");

		Files.write(htmlPath, sb.toString().getBytes(StandardCharsets.UTF_8));
	}

	private static String metricsTable(List<Row> rows, boolean highlight) {
		boolean sortable = !highlight;
		StringBuilder sb = new StringBuilder();
		sb.append("<table").append(sortable ? " class=\"sortable-table\"" : "").append(">\n<thead><tr>\n");
		sb.append(sortableHeader("Element (元素)", sortable));
		sb.append(sortableHeader("Instructions (指令覆盖率)", sortable));
		sb.append(sortableHeader("Branches (分支覆盖率)", sortable));
		sb.append(sortableHeader("Complexity (圈复杂度覆盖率)", sortable));
		sb.append(sortableHeader("Lines (行覆盖率)", sortable));
		sb.append(sortableHeader("Methods (方法覆盖率)", sortable));
		sb.append(sortableHeader("Classes (类覆盖率)", sortable));
		sb.append("</tr></thead>\n<tbody>\n");

		for (Row row : rows) {
			sb.append("<tr").append(highlight ? " class=\"total\"" : "").append(">\n");
			sb.append("<td data-sort=\"").append(escapeAttr(row.name)).append("\">")
					.append(escape(row.name)).append("</td>\n");
			sb.append(metricCell(row.metrics.instrMissed, row.metrics.instrCovered));
			sb.append(metricCell(row.metrics.branchMissed, row.metrics.branchCovered));
			sb.append(metricCell(row.metrics.cxtyMissed, row.metrics.cxtyCovered));
			sb.append(metricCell(row.metrics.lineMissed, row.metrics.lineCovered));
			sb.append(metricCell(row.metrics.methodMissed, row.metrics.methodCovered));
			sb.append(metricCell(row.metrics.classMissed, row.metrics.classCovered));
			sb.append("</tr>\n");
		}

		sb.append("</tbody>\n</table>\n");
		return sb.toString();
	}

	private static String sortableHeader(String label, boolean sortable) {
		if (!sortable) {
			return "<th>" + label + "</th>\n";
		}
		return "<th class=\"sortable\">" + label + "</th>\n";
	}

	private static String metricCell(long missed, long covered) {
		long total = missed + covered;
		boolean na = total == 0;
		String percent = pct(missed, covered);
		String counts = na ? "-" : missed + "/" + total;
		String sortValue = sortValue(missed, covered);
		String title = na ? "no applicable data" : missed + " missed, " + total + " total";
		String barHtml;
		if (na) {
			barHtml = "<span class=\"bar na\"></span>";
		} else {
			double ratio = (double) covered / total;
			barHtml = "<span class=\"bar\"><span class=\"fill\" style=\"width:"
					+ String.format(Locale.US, "%.0f", ratio * 100) + "%\"></span></span>";
		}
		return "<td class=\"metric" + (na ? " na" : "") + "\" data-sort=\"" + sortValue + "\" title=\"" + title
				+ "\">"
				+ "<span class=\"metric-line\">"
				+ barHtml
				+ "<span class=\"pct\">" + percent + "</span>"
				+ "<span class=\"cnt\">" + counts + "</span>"
				+ "</span></td>\n";
	}

	private static String pct(long missed, long covered) {
		long total = missed + covered;
		if (total == 0) {
			return "n/a";
		}
		return String.format(Locale.US, "%.2f%%", covered * 100.0 / total);
	}

	private static String sortValue(long missed, long covered) {
		long total = missed + covered;
		if (total == 0) {
			return "-1";
		}
		return String.format(Locale.US, "%.6f", covered * 100.0 / total);
	}

	private static String sortScript() {
		return "<script>\n"
				+ "(function(){document.querySelectorAll('table.sortable-table').forEach(function(table){"
				+ "var headers=table.querySelectorAll('th.sortable');"
				+ "headers.forEach(function(th,colIdx){"
				+ "th.addEventListener('click',function(){"
				+ "var tbody=table.tBodies[0];"
				+ "var rows=Array.prototype.slice.call(tbody.rows);"
				+ "var asc=th.classList.contains('sort-asc');"
				+ "headers.forEach(function(h){h.classList.remove('sort-asc','sort-desc');});"
				+ "var sortAsc=!asc;"
				+ "th.classList.add(sortAsc?'sort-asc':'sort-desc');"
				+ "rows.sort(function(a,b){"
				+ "var av=a.cells[colIdx].getAttribute('data-sort')||'';"
				+ "var bv=b.cells[colIdx].getAttribute('data-sort')||'';"
				+ "var an=parseFloat(av),bn=parseFloat(bv);"
				+ "var cmp;"
				+ "if(!isNaN(an)&&!isNaN(bn)){cmp=an-bn;}"
				+ "else{cmp=av.localeCompare(bv);}"
				+ "return sortAsc?cmp:-cmp;"
				+ "});"
				+ "rows.forEach(function(r){tbody.appendChild(r);});"
				+ "});"
				+ "});"
				+ "});})();\n"
				+ "</script>\n";
	}

	private static String css() {
		return ""
				+ "body{font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;margin:16px 20px;color:#222;}\n"
				+ "h1{margin:0 0 6px;font-size:1.35rem;}\n"
				+ "h2{margin:18px 0 8px;font-size:1rem;border-bottom:1px solid #ddd;padding-bottom:4px;}\n"
				+ ".meta{color:#666;margin-bottom:12px;font-size:0.88rem;}\n"
				+ "table{border-collapse:collapse;width:100%;font-size:0.82rem;table-layout:fixed;}\n"
				+ "th,td{border:1px solid #ddd;padding:3px 6px;vertical-align:middle;line-height:1.2;}\n"
				+ "th{background:#f5f5f5;text-align:left;font-size:0.8rem;}\n"
				+ "th.sortable{cursor:pointer;padding-right:16px;background-repeat:no-repeat;background-position:right center;"
				+ "background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='10'%3E"
				+ "%3Cpath fill='%23999' d='M4 0L7 4H1z'/%3E%3Cpath fill='%23999' d='M4 10L1 6h6z'/%3E%3C/svg%3E\");}\n"
				+ "th.sort-asc{background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='10'%3E"
				+ "%3Cpath fill='%23333' d='M4 0L7 4H1z'/%3E%3Cpath fill='%23ccc' d='M4 10L1 6h6z'/%3E%3C/svg%3E\");}\n"
				+ "th.sort-desc{background-image:url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='8' height='10'%3E"
				+ "%3Cpath fill='%23ccc' d='M4 0L7 4H1z'/%3E%3Cpath fill='%23333' d='M4 10L1 6h6z'/%3E%3C/svg%3E\");}\n"
				+ "td:first-child,th:first-child{width:22%;word-break:break-all;}\n"
				+ "tr.total td{background:#f0f7ff;font-weight:600;}\n"
				+ "tr:nth-child(even):not(.total){background:#fafafa;}\n"
				+ "td.metric{padding:2px 4px;}\n"
				+ ".metric-line{display:flex;align-items:center;gap:5px;white-space:nowrap;}\n"
				+ ".bar{flex:0 0 44px;width:44px;height:6px;background:#f8d7da;border-radius:0;overflow:hidden;display:block;}\n"
				+ ".bar.na{background:#ddd;}\n"
				+ ".fill{display:block;height:100%;min-width:0;background:#28a745;}\n"
				+ "td.metric.na .pct{color:#999;font-weight:normal;}\n"
				+ "td.metric.na .cnt{color:#bbb;}\n"
				+ ".pct{flex:0 0 auto;font-weight:600;font-size:0.8rem;}\n"
				+ ".cnt{flex:1 1 auto;color:#888;font-size:0.75rem;text-align:right;}\n"
				+ "code{background:#f4f4f4;padding:1px 3px;border-radius:2px;}\n";
	}

	private static String escape(String s) {
		if (s == null) {
			return "";
		}
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;");
	}

	private static String escapeAttr(String s) {
		return escape(s);
	}

	private static final class Row {
		final String name;
		final Metrics metrics;

		Row(String name, Metrics metrics) {
			this.name = name;
			this.metrics = metrics;
		}
	}

	private static final class Metrics {
		long instrMissed;
		long instrCovered;
		long branchMissed;
		long branchCovered;
		long cxtyMissed;
		long cxtyCovered;
		long lineMissed;
		long lineCovered;
		long methodMissed;
		long methodCovered;
		long classMissed;
		long classCovered;

		boolean isEmpty() {
			return instrMissed + instrCovered + branchMissed + branchCovered
					+ lineMissed + lineCovered + methodMissed + methodCovered
					+ classMissed + classCovered + cxtyMissed + cxtyCovered == 0;
		}

		void add(Metrics other) {
			instrMissed += other.instrMissed;
			instrCovered += other.instrCovered;
			branchMissed += other.branchMissed;
			branchCovered += other.branchCovered;
			cxtyMissed += other.cxtyMissed;
			cxtyCovered += other.cxtyCovered;
			lineMissed += other.lineMissed;
			lineCovered += other.lineCovered;
			methodMissed += other.methodMissed;
			methodCovered += other.methodCovered;
			classMissed += other.classMissed;
			classCovered += other.classCovered;
		}

		static Metrics fromColumns(String[] cols, int[] index) {
			Metrics m = new Metrics();
			m.instrMissed = parseLong(col(cols, index, 3));
			m.instrCovered = parseLong(col(cols, index, 4));
			m.branchMissed = parseLong(col(cols, index, 5));
			m.branchCovered = parseLong(col(cols, index, 6));
			m.lineMissed = parseLong(col(cols, index, 7));
			m.lineCovered = parseLong(col(cols, index, 8));
			m.cxtyMissed = parseLong(col(cols, index, 9));
			m.cxtyCovered = parseLong(col(cols, index, 10));
			m.methodMissed = parseLong(col(cols, index, 11));
			m.methodCovered = parseLong(col(cols, index, 12));
			return m;
		}

		/** JaCoCo CSV 无 CLASS 列，按「类行是否有覆盖」推算类覆盖率。 */
		void applyClassCoverageFromRow() {
			if (instrCovered > 0 || lineCovered > 0 || methodCovered > 0) {
				classCovered = 1;
			} else {
				classMissed = 1;
			}
		}

		private static long parseLong(String s) {
			if (s == null || s.isEmpty()) {
				return 0L;
			}
			return Long.parseLong(s);
		}
	}
}
