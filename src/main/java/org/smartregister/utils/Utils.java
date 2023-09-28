package org.smartregister.utils;

import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static String cleanIdString(String idString) {
        return StringUtils.isNotBlank(idString) && idString.contains(Constants.SLASH_UNDERSCORE) ?
                idString.substring(0, idString.indexOf(Constants.SLASH_UNDERSCORE)) : idString;
    }
}
