/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrControllerInfo implements VrActionInfo{

    private String title;
    private String subTitle;

    private String url;
    private String menuPath;
    private String source;
    private String securityKey;

    private String css;
    private VrActionEnabler enabler;

    private BreadcrumbItem[] breadcrumb;
    private int priority;

    public VrControllerInfo() {
    }

    public VrControllerInfo(String title, String subTitle, String menuPath,String url, String css, String securityKey, BreadcrumbItem... breadcrumb) {
        this.title = title;
        this.subTitle = subTitle;
        this.menuPath = menuPath;
        this.url = url;
        this.css = css;
        this.securityKey = securityKey;
        this.breadcrumb = breadcrumb;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public BreadcrumbItem[] getBreadcrumb() {
        return breadcrumb;
    }

    public VrControllerInfo setBreadcrumb(BreadcrumbItem[] breadcrumb) {
        this.breadcrumb = breadcrumb;
        return this;
    }

    public String getCss() {
        return css;
    }

    public VrControllerInfo setCss(String css) {
        this.css = css;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VrControllerInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public VrControllerInfo setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public VrControllerInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public VrActionEnabler getEnabler() {
        return enabler;
    }

    public VrControllerInfo setEnabler(VrActionEnabler enabler) {
        this.enabler = enabler;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
