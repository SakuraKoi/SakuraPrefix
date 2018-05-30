package ldcr.LuckyPrefix;

public class PrefixData {
	private final String player;
	private String prefix;
	private String suffix;
	private String tagPrefix;
	private String tagSuffix;
	private String nick;
	public PrefixData(final String player, final String prefix, final String suffix, final String tagPrefix, final String tagSuffix, final String nick) {
		this.player = player;
		this.prefix = prefix;
		this.suffix = suffix;
		this.tagPrefix = tagPrefix;
		this.tagSuffix = tagSuffix;
		this.nick = nick;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		if (!prefix.isEmpty()) {
			prefix = processColor(prefix+" ");
			prefix = processChar(prefix);
		}
		this.prefix = prefix;
		if (tagPrefix.isEmpty() || ((tagPrefix.startsWith("§")) && (tagPrefix.length()==2))) {
			if (prefix.startsWith("§") && (prefix.length()>=2)) {
				tagPrefix = prefix.substring(0, 2);
			} else {
				tagPrefix = "";
			}
		}
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		if (!suffix.isEmpty()) {
			suffix = processColor(" "+suffix);
			suffix = processChar(suffix);
		}
		this.suffix = suffix;
	}
	public String getTagPrefix() {
		return tagPrefix;
	}
	public void setTagPrefix(String prefix) {
		if (!prefix.isEmpty()) {
			prefix = processColor(prefix);
			prefix = processChar(prefix);
			if ((prefix.charAt(0)!='§') || (prefix.length()!=2)) {
				prefix = prefix+" ";
			}
		}
		tagPrefix = prefix;
	}
	public String getTagSuffix() {
		return tagSuffix;
	}
	public void setTagSuffix(String suffix) {
		if (!suffix.isEmpty()) {
			suffix = processColor(" "+suffix);
			suffix = processChar(suffix);
		}
		tagSuffix = suffix;
	}
	public String getPlayer() {
		return player;
	}
	public static String processColor(final String str) {
		return str.replace('&', '§');
	}
	public String getNick() {
		return nick;
	}
	public void setNick(final String nick) {
		this.nick = nick;
	}
	private String processChar(final String str) {
		return str.replace("\\[", "『").replace("\\]", "』");
	}
}
