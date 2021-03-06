package net.vpc.app.vainruling.core.service.model.strict;

import net.vpc.app.vainruling.core.service.model.AppUser;

public class AppUserStrict {
    private int id;
    private String login;
    private String fullName;
    private String fullTitle;
    private String genderCode;

    public AppUserStrict() {
    }

    public AppUserStrict(AppUser u) {
        if (u != null) {
            id = u.getId();
            login = u.getLogin();
            if (u.getContact() != null) {
                fullName = u.getContact().getFullName();
                fullTitle = u.getContact().getFullTitle();
                if (u.getContact().getGender() != null) {
                    genderCode = u.getContact().getGender().getCode();
                }
            }
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }
}
