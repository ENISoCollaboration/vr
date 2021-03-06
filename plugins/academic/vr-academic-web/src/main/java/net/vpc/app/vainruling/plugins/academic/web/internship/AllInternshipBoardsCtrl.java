/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.web.internship;

import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPluginSecurity;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.service.model.internship.ext.AcademicInternshipExtList;

import java.util.ArrayList;
import java.util.List;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@VrController(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
//        css = "fa-table",
//        title = "Tous les Stages",
        menu = "/Education/Projects/Internships",
        securityKey = AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_ALL_INTERNSHIPS,
        url = "modules/academic/internship/all-internships"
)
public class AllInternshipBoardsCtrl extends MyInternshipBoardsCtrl {

    @OnPageLoad
    public void onPageLoad() {
        super.onPageLoad();
    }

    @Override
    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = p.findTeacher(teacherId);
        if (t != null && t.getDepartment() != null) {
            return pi.findEnabledInternshipBoardsByDepartment(-1,t.getDepartment().getId(),true);
        }
        return new ArrayList<>();
    }

    @Override
    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicPlugin pi = VrApp.getBean(AcademicPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(-1, (t != null && t.getDepartment() != null) ? t.getDepartment().getId() : -1, -1, internshipTypeId, boardId,
                true);
    }

    public void addToMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.addSupervisorIntent(ii.getInternship().getId(), c.getId());
            ii.rewrap(getCurrentTeacher());
        }
    }

    public void removeFromMine(AcademicInternshipInfo ii) {
        AcademicTeacher c = getCurrentTeacher();
        if (c != null) {
            AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
            p.removeSupervisorIntent(ii.getInternship().getId(), c.getId());
            ii.rewrap(getCurrentTeacher());
        }
    }

}
