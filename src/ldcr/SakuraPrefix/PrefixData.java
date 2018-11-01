/**
 * @Project SakuraPrefix
 *
 * Copyright 2018 Ldcr. All right reserved.
 *
 * This is a private project. Distribution is not allowed.
 * You needs ask Ldcr for the permission to using it on your server.
 * 
 * @Author Ldcr (ldcr993519867@gmail.com)
 */
package ldcr.SakuraPrefix;

import lombok.Getter;

public class PrefixData {
	@Getter private final String player;
	@Getter private String prefix;
	@Getter private String suffix;
	@Getter private String tagPrefix;
	@Getter private String tagSuffix;
	@Getter private String nick;
	@Getter private boolean locked;

	@Getter private boolean changedPrefix = false;
	@Getter private boolean changedSuffix = false;
	@Getter private boolean changedTagPrefix = false;
	@Getter private boolean changedTagSuffix = false;
	@Getter private boolean changedNick = false;
	@Getter private boolean changedLocked = false;
	public PrefixData(final String player, final String prefix, final String suffix, final String tagPrefix, final String tagSuffix, final String nick, final boolean locked) {
		this.player = player;
		this.prefix = prefix;
		this.suffix = suffix;
		this.tagPrefix = tagPrefix;
		this.tagSuffix = tagSuffix;
		this.nick = nick;
		this.locked = locked;
	}
	public void setPrefix(String prefix) {
		changedPrefix = true;
		if (!prefix.isEmpty()) {
			prefix = processColor(prefix+" ");
			prefix = processChar(prefix);
		}
		this.prefix = prefix;
		if (tagPrefix.isEmpty() || tagPrefix.startsWith("§") && tagPrefix.length()==2) {
			if (prefix.startsWith("§") && prefix.length()>=2) {
				tagPrefix = prefix.substring(0, 2);
			} else {
				tagPrefix = "";
			}
		}
	}
	public void setSuffix(String suffix) {
		changedSuffix = true;
		if (!suffix.isEmpty()) {
			suffix = processColor(" "+suffix);
			suffix = processChar(suffix);
		}
		this.suffix = suffix;
	}
	public void setTagPrefix(String prefix) {
		changedTagPrefix = true;
		if (!prefix.isEmpty()) {
			prefix = processColor(prefix);
			prefix = processChar(prefix);
			if (prefix.charAt(0)!='§' || prefix.length()!=2) {
				prefix = prefix+" ";
			}
		}
		tagPrefix = prefix;
	}
	public void setTagSuffix(String suffix) {
		changedTagSuffix = true;
		if (!suffix.isEmpty()) {
			suffix = processColor(" "+suffix);
			suffix = processChar(suffix);
		}
		tagSuffix = suffix;
	}
	public void setNick(final String nick) {
		changedNick = true;
		this.nick = nick;
	}
	public void setLocked(final boolean locked) {
		changedLocked = true;
		this.locked = locked;
	}


	public static String processColor(final String str) {
		return str.replace('&', '§');
	}
	private String processChar(final String str) {
		return str.replace("{", "『").replace("}", "』");
	}
}
