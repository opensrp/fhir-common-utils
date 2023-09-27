package org.smartregister.utils;

import org.apache.commons.lang3.StringUtils;

import static org.smartregister.utils.Constants.SLASH_UNDERSCORE;

public class Utils {
    public static String cleanIdString(String idString) {
        if (StringUtils.isNotBlank(idString) && idString.contains(SLASH_UNDERSCORE)) {
            idString = idString.substring(0, idString.indexOf(SLASH_UNDERSCORE));
        }
        return idString;
    }
}
