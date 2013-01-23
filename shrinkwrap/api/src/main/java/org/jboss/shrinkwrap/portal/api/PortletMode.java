package org.jboss.shrinkwrap.portal.api;

/**
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
public enum PortletMode {
    VIEW("view"),
    EDIT("edit"),
    HELP("help");

    private String value;

    PortletMode(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public static String[] valuesAsString(PortletMode... modes) {
        String[] modeStrings = new String[modes.length];
        int i = 0;
        for (PortletMode mode : modes) {
            modeStrings[i++] = mode.toString();
        }
        return modeStrings;
    }

    public static PortletMode getFromStringValue(String value) {
        for (PortletMode mode : PortletMode.values()) {
            if (null != value && mode.toString().equals(value)) {
                return mode;
            }
        }
        return null;
    }
}
