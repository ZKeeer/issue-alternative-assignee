package com.iamhuy.jira.plugin.customfields;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.atlassian.jira.user.ApplicationUser;

final class UserDisplayNameComparator implements Comparator<ApplicationUser> {

    private final Collator collator;

    public UserDisplayNameComparator(Locale locale) {
        this.collator = Collator.getInstance(locale);
        // Make this case insensitive
        this.collator.setStrength(Collator.SECONDARY);
    }

    public int compare(final ApplicationUser user1, final ApplicationUser user2) {
        if ((user1 == null) && (user2 == null)) {
            return 0;
        } else if (user2 == null) {
            return -1;
        } else if (user1 == null) {
            return 1;
        }
        String name1 = user1.getDisplayName();
        String name2 = user2.getDisplayName();
        if (name1 == null || name2 == null) {
            throw new RuntimeException("Null user name");
        }
        return collator.compare(name1, name2);
    }
}
