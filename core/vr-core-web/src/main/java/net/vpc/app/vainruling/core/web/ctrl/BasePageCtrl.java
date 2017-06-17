/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.ctrl;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.UCtrlData;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;

/**
 * @author taha.bensalah@gmail.com
 */
public abstract class BasePageCtrl extends BaseCtrl {

    public BreadcrumbItem getTitle() {
        UCtrlData c = VrApp.getBean(VrMenuManager.class).getUCtrlDataByObj(this, null);

        return new BreadcrumbItem(c == null ? "" : c.getTitle()
                , c == null ? "" : c.getSubTitle()
                , c == null ? "" : c.getCss()
                , ""
                , "");
    }

}
