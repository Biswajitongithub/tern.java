package tern.angular.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tern.angular.AngularType;
import tern.utils.StringUtils;

public class Directive {

	private final String name;
	private final AngularType type;
	private final String url;
	private final Collection<UseAs> useAs;
	private final Module module;
	private final Collection<String> tagNames;
	private final String description;
	private final boolean optionnal;

	public Directive(String name, AngularType type, String url,
			Collection<String> tagNames, Collection<UseAs> useAs,
			boolean optionnal, String description, Module module) {
		this.name = name;
		this.type = type;
		this.url = url;
		this.useAs = useAs;
		this.optionnal = optionnal;
		this.module = module;
		this.tagNames = tagNames;
		this.description = description;
		if (module != null) {
			module.addDirective(this);
		}
	}

	public String getName() {
		return name;
	}

	public AngularType getType() {
		return type;
	}

	public Collection<String> getTagNames() {
		return tagNames;
	}

	public Collection<String> getNames() {
		StringBuilder prefix = new StringBuilder();
		StringBuilder suffix = new StringBuilder();
		char[] chars = name.toCharArray();
		char c = 0;
		for (int i = 0; i < chars.length; i++) {
			c = chars[i];
			if (suffix.length() > 0) {
				suffix.append(c);
			} else {
				if (Character.isUpperCase(c)) {
					suffix.append(Character.toLowerCase(c));
				} else {
					prefix.append(c);
				}
			}
		}
		String moduleName = prefix.toString();
		String directiveName = suffix.toString();

		List<String> names = new ArrayList<String>();
		// ex ngBind
		names.add(name);
		for (Character delimiter : DirectiveHelper.DELIMITERS) {
			for (String startsWith : DirectiveHelper.STARTS_WITH) {
				names.add(new StringBuilder(startsWith).append(moduleName)
						.append(delimiter).append(directiveName).toString());
			}
		}

		return names;
	}

	public Module getModule() {
		return module;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public Collection<UseAs> getUseAs() {
		return useAs;
	}

	public String getHTMLDescription() {
		StringBuilder info = new StringBuilder("");
		info.append("<b>");
		info.append(getName());
		info.append("</b>");
		info.append(" directive in module ");
		info.append("<b>");
		info.append(getModule().getName());
		info.append("</b>");
		if (!StringUtils.isEmpty(description)) {
			info.append("<br/>");
			info.append("<br/>");
			info.append(description);
		}
		if (!StringUtils.isEmpty(url)) {
			info.append("<br/>");
			info.append("<br/>");
			info.append("<b>@see</b> ");
			info.append(url);
		}
		return info.toString();
	}

	public boolean isOptionnal() {
		return optionnal;
	}
}
