/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web.jsf;

import net.vpc.app.vainruling.core.service.content.ContentText;
import net.vpc.app.vainruling.core.web.DummyMessageTextService;
import net.vpc.app.vainruling.core.web.DummyTaskTextService;
import net.vpc.app.vainruling.core.web.util.StrLabel;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.content.*;
import net.vpc.app.vainruling.core.service.model.AppProperty;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.security.UserSessionInfo;
import net.vpc.app.vainruling.core.service.security.UserToken;
import net.vpc.app.vainruling.core.service.util.VrUPAUtils;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.service.util.wiki.VrWikiParser;
import net.vpc.app.vainruling.core.web.jsf.converters.EntityConverter;
import net.vpc.app.vainruling.core.web.jsf.ctrl.ActiveSessionsCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.AppGlobalCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.LoginCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentUploadListener;
import net.vpc.app.vainruling.core.web.jsf.ctrl.DocumentsCtrl;
import net.vpc.app.vainruling.core.web.jsf.ctrl.dialog.DocumentsUploadDialogCtrl;
import net.vpc.app.vainruling.core.web.menu.BreadcrumbItem;
import net.vpc.app.vainruling.core.web.menu.VRMenuInfo;
import net.vpc.app.vainruling.core.web.menu.VrMenuManager;
import net.vpc.app.vainruling.core.web.themes.VrTheme;
import net.vpc.app.vainruling.core.web.util.VrWebHelper;
import net.vpc.common.io.IOUtils;
import net.vpc.common.io.PathInfo;
import net.vpc.common.jsf.FacesUtils;
import net.vpc.common.strings.StringUtils;
import net.vpc.common.util.CollectionUtils;
import net.vpc.common.util.BytesSizeFormatter;
import net.vpc.common.util.Utils;
import net.vpc.common.vfs.VFile;
import net.vpc.common.vfs.VFileFilter;
import net.vpc.common.vfs.VFileVisitor;
import net.vpc.common.vfs.VirtualFileSystem;
import net.vpc.upa.Action;
import net.vpc.upa.Entity;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Controller;

/**
 * @author taha.bensalah@gmail.com
 */
@Scope(value = "singleton")
@Controller
public class Vr {

    public static final Object NullSelected = new Object();

    public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");
    public static final String NULL_VALUE_STR = "-*Aucune Valeur*-";



    private MessageTextService messageTextService;
    private TaskTextService taskTextService;
    private NotificationTextService notificationTextService;
    private CmsTextService cmsTextService;
    @Autowired
    private CorePlugin core;
    private WeakHashMap<String, DecimalFormat> decimalFormats = new WeakHashMap<>();

    public Vr() {
    }

    public static Vr get() {
        return VrApp.getBean(Vr.class);
    }

    public VrTheme getAppPublicTheme() {
        return VrWebHelper.getAppPublicTheme();
    }

    public VrTheme getAppPrivateTheme() {
        return VrWebHelper.getAppPrivateTheme();
    }

    public void setHttpRequestAttribute(String name, Object value) {
        getHttpRequest().setAttribute(name, value);
    }

    public HttpServletRequest getHttpRequest() {
        return VrWebHelper.getHttpServletRequest();
    }

    public HttpSession getHttpSession() {
        HttpServletRequest r = VrWebHelper.getHttpServletRequest();
        return r == null ? null : r.getSession(true);
    }

    public VrTheme getUserPublicTheme(String login) {
        return VrWebHelper.getUserPublicTheme(login);
    }

    public VrTheme getUserPrivateTheme(String login) {
        return VrWebHelper.getUserPrivateTheme(login);
    }

    public AppUser getCurrentUser() {
        return CorePlugin.get().getCurrentUser();
    }

    public AppUser getCurrentRootUser() {
        UserToken s = CorePlugin.get().getCurrentToken();
        if (s != null) {
            Integer id = s.getRootUserId();
            if (id == null) {
                return null;
            }
            return UPA.getPersistenceUnit().invokePrivileged(new Action<AppUser>() {
                @Override
                public AppUser run() {
                    return core.findUser(id);
                }
            });
        }
        return null;
    }

    public VrTheme getPublicTheme() {
        return VrWebHelper.getPublicTheme();
    }

    public VrTheme getPrivateTheme() {
        return VrWebHelper.getPrivateTheme();
    }

    public <T> List<T> listHead(List<T> anyList, int maxSize) {
        return CollectionUtils.head(anyList, maxSize);
    }

    public <T> List<T> listTail(List<T> anyList, int maxSize) {
        return CollectionUtils.tail(anyList, maxSize);
    }

    public <T> List<List<T>> splitListBy(int groupSize, List<T> anyList) {
        return CollectionUtils.splitBy(anyList, groupSize);
    }

    public <T> List<List<T>> groupListBy(int groupSize, List<T> anyList) {
        return CollectionUtils.groupBy(anyList, groupSize);
    }

    public int randomize(int a) {
        return (int) (Math.random() * a);
    }

    public int randomize(long a) {
        return (int) (Math.random() * a);
    }

    public double randomize(double a) {
        return (Math.random() * a);
    }

    public String randomize(String... items) {
        return items[randomize(items.length)];
    }

    public Object randomizeList(List items) {
        int size = items == null ? 0 : items.size();
        if (size == 0) {
            return null;
        }
        return items.get(randomize(size));
    }

    public String trim(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString().trim();
    }

    public List listFlattenAndTrimAndAppend(Object... objectsOrLists) {
        List any = new ArrayList();
        if (objectsOrLists != null) {
            for (Object objectOtList : objectsOrLists) {
                if (objectOtList != null) {
                    if (objectOtList instanceof List) {
                        for (Object o : ((List) objectOtList)) {
                            if (o != null) {
                                any.add(o);
                            }
                        }
                    } else if (objectOtList.getClass().isArray()) {
                        int length = Array.getLength(objectOtList);
                        for (int i = 0; i < length; i++) {
                            Object o = Array.get(objectOtList, i);
                            if (o != null) {
                                any.add(o);
                            }
                        }
                    } else {
                        any.add(objectOtList);
                    }
                }
            }
        }
        return any;
    }

    public List<ContentPath> findImageAttachments(List<ContentPath> paths) {
        List<ContentPath> filtered = new ArrayList<>();
        if (paths != null) {
            LinkedHashSet<String> all = new LinkedHashSet<>();
            for (ContentPath path : paths) {
                if (path != null && !StringUtils.isEmpty(path.getPath())) {
                    if (!all.contains(path.getPath())) {
                        all.add(path.getPath());
                    }
                }
            }
            HashSet<String> ok = new HashSet<>(findValidImages(all.toArray(new String[all.size()])));
            for (ContentPath path : paths) {
                if (path != null && ok.contains(path.getPath())) {
                    filtered.add(path);
                }
            }
        }
        return filtered;
    }

    public List<ContentPath> findNonImageAttachments(List<ContentPath> paths) {
        List<ContentPath> filtered = new ArrayList<>();
        if (paths != null) {
            LinkedHashSet<String> all = new LinkedHashSet<>();
            for (ContentPath path : paths) {
                if (path != null && !StringUtils.isEmpty(path.getPath())) {
                    if (!all.contains(path.getPath())) {
                        all.add(path.getPath());
                    }
                }
            }
            HashSet<String> ok = new HashSet<>(findNonImages(all.toArray(new String[all.size()])));
            for (ContentPath path : paths) {
                if (path != null && ok.contains(path.getPath())) {
                    filtered.add(path);
                }
            }
        }
        return filtered;
    }

    public List<String> findValidImages(String... paths) {
        return findValidFiles("**.(png|jpg|jpeg|gif)", true, paths);
    }

    public List<String> findNonImages(String... paths) {
        return findValidFiles("**.(png|jpg|jpeg|gif)", false, paths);
    }

    public List<String> findValidFiles(String expression, boolean positive, String... paths) {
        return UPA.getContext().invokePrivileged(new Action<List<String>>() {
            @Override
            public List<String> run() {
                VirtualFileSystem fs = core.getRootFileSystem();
                LinkedHashSet<String> all = new LinkedHashSet<>();
                Pattern patternObj = Pattern.compile(StringUtils.wildcardToRegex(expression, '/'), Pattern.CASE_INSENSITIVE);
                VFileFilter filter = new VFileFilter() {
                    @Override
                    public boolean accept(VFile pathname) {
                        return pathname.isDirectory() || (positive == patternObj.matcher(pathname.getPath()).matches());
                    }
                };
                for (String path : paths) {
                    if (positive == (patternObj.matcher(path).matches())) {
                        if (!all.contains(path)) {
                            all.add(path);
                        }
                    } else {
                        String urlProtocol = IOUtils.getUrlProtocol(path);
                        if (urlProtocol == null) {
                            //ok;
                        } else if (urlProtocol.equals("file")) {
                            path = IOUtils.getUrlFile(path);
                        } else {
                            continue;
                        }
                        VFile vFile = fs.get(path);
                        if (vFile != null) {
                            if (vFile.isFile()) {
                                //check match
                                if (filter.accept(vFile)) {
                                    String p = vFile.getPath();
                                    if (!all.contains(p)) {
                                        all.add(p);
                                    }
                                }
                            } else if (vFile.isDirectory()) {
                                vFile.visit(new VFileVisitor() {
                                    @Override
                                    public boolean visit(VFile pathname) {
                                        if (pathname.isFile()) {
                                            String p = pathname.getPath();
                                            if (!all.contains(p)) {
                                                all.add(p);
                                            }
                                        }
                                        return true;
                                    }
                                }, filter);
                            }
                        }
                    }
                }
                return new ArrayList<>(all);
            }
        });

    }

    public boolean isFSPath(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        return !(path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://"));
    }

    public double abs(double a) {
        return Math.abs(a);
    }

    public double max(double a, double b) {
        return Math.max(a, b);
    }

    public double min(double a, double b) {
        return Math.min(a, b);
    }

    public int rand(int a) {
        return (int) (Math.random() * a);
    }

    public double rand(double a) {
        return (Math.random() * a);
    }

    public String rand(String... values) {
        return values[randomize(values.length)];
    }

    public boolean contextTextHasDecoration(ContentText text, String decoration) {
        if (text != null && !StringUtils.isEmpty(decoration)) {
            String decoration1 = text.getDecoration();
            if (decoration1 != null) {
                decoration1 = decoration1.toLowerCase().trim();
                String trimmed = decoration.toLowerCase().trim();
                if (StringUtils.indexOfWord(decoration1, trimmed) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public int hashToMax(Object value, int max) {
        return Math.abs(value == null ? 0 : value.hashCode()) % max;
    }

    public String hashToArr(Object value, String... values) {
        return values[hashToMax(value, values.length)];
    }

    public double frame(double x, double a, double b) {
        if (x < a) {
            x = a;
        }
        if (x > b) {
            x = b;
        }
        return x;
    }

    public String articleImageOrRand(String imageURL) {
        if (imageURL != null) {
            String low = imageURL.toLowerCase();
            for (String s : new String[]{"jpg", "jpeg", "png", "gif"}) {
                if (low.endsWith("." + s) || low.contains("." + s + "?")) {
                    return url(imageURL);
                }
            }
            for (String s : new String[]{"xls", "xlsx", "pdf", "html", "xhtml", "doc", "docx", "js", "csv", "log", "text", "xml", "zip"}) {
                if (low.endsWith("." + s) || low.contains("." + s + "?")) {
                    return url("/Site/images/icons/file-" + s + "16.png");
                }
            }
            imageURL = "";
        }
        return url((String) nvl(imageURL, strFormat("/Site/images/articles/%1s",
                randomize(
                        "article-01.jpg",
                        "article-02.jpg",
                        "article-03.jpg",
                        "article-04.jpg",
                        "article-05.jpg",
                        "article-06.jpg",
                        "article-07.jpg",
                        "article-08.jpg",
                        "article-09.jpg",
                        "article-10.jpg"
                )
        )));
    }

    public String articleImageOrDefault(String imageURL) {
        return url((String) nvl(imageURL, strFormat("/Site/images/articles/%1s",
                "article-01.jpg"
        )));
    }

    public List<String> contentPathToFSUrlList(List<ContentPath> paths) {
        List<String> ret = new ArrayList<>(paths.size());
        for (ContentPath path : paths) {
            ret.add(url(path.getPath()));
        }
        return ret;
    }

    public List<String> fsurlList(List<String> paths) {
        List<String> ret = new ArrayList<>(paths.size());
        for (String path : paths) {
            ret.add(url(path));
        }
        return ret;
    }

    public String url(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://")) {
            return path;
        }
        if (path.startsWith("site://")) {
            String s = path.substring("site://".length());
            if (!s.startsWith("/")) {
                s = "/" + s;
            }
            return getContext() + s;
        }
        if (path.startsWith("article://")) {
            String s = path.substring("article://".length());
            if (!s.startsWith("/")) {
                s = "/p/news?a={id=" + s + "}";
            }
            return getContext() + s;
        }
        if (path.startsWith("context://")) {
            String s = path.substring("context://".length());
            if (!s.startsWith("/")) {
                s = "/" + s;
            }
            return getContext() + s;
        }
        if (path.startsWith("public-theme-context://")) {
            String s = path.substring("public-theme-context://".length());
            if (!s.startsWith("/")) {
                s = "/" + s;
            }
            return getPublicThemeContext() + s;
        }
        if (path.startsWith("private-theme-context://")) {
            String s = path.substring("private-theme-context://".length());
            if (!s.startsWith("/")) {
                s = "/" + s;
            }
            return getPrivateThemeContext() + s;
        }
        if (path.startsWith("file://")) {
            String s = path.substring("file://".length());
            String prefix = getContext() + "/fs";
            if (!s.startsWith("/")) {
                prefix += "/";
            }
            return prefix + s;
        }
        String prefix = getContext() + "/fs";
        if (!path.startsWith("/")) {
            prefix += "/";
        }
        return prefix + path;
    }

    public String exurl(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        path = path.trim();
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://")) {
            return path;
        }
        if (path.contains("://")) {
            return path;
        }
        return "http://" + path;
    }

    /**
     * relative url, no context prefixed
     *
     * @param path
     * @return
     */
    public String urlr(String path) {
        if (path == null) {
            path = "";
        }
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("ftp://")) {
            return path;
        }
        String prefix = "/fs";
        if (!path.startsWith("/")) {
            prefix += "/";
        }
        return prefix + path;
    }

    public boolean isEmpty(Object... a) {
        return str(a).trim().length() == 0;
    }

    public String str(Object... a) {
        StringBuilder sb = new StringBuilder();
        if (a != null) {
            for (Object x : a) {
                if (x != null) {
                    sb.append(String.valueOf(x));
                }
            }
        }
        return sb.toString();
    }

    public String nvlstr(Object... a) {
        if (a != null) {
            for (Object x : a) {
                String s = x == null ? "" : String.valueOf(x);
                if (!StringUtils.isEmpty(s)) {
                    return s;
                }
            }
        }
        return "";
    }

    public Object nvl(Object... a) {
        for (Object x : a) {
            if (x != null) {
                if (x instanceof String) {
                    if (!StringUtils.isEmpty((String) x)) {
                        return x;
                    }
                } else {
                    return x;
                }
            }
        }
        return null;
    }

    public String strFormat(String format, Object... args) {
        return String.format(format, args);
    }

    public String strcat(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (x != null) {
                if (x instanceof String) {
                    if (!StringUtils.isEmpty((String) x)) {
                        sb.append((String) x);
                    }
                } else {
                    sb.append(x);
                }
            }
        }
        return sb.toString();
    }

    public String strcatsep(String sep, Object... a) {
        return VrUtils.strcatsep(sep, a);
    }

    public String fstr(String format, Object... a) {
        MessageFormat mf = new MessageFormat(format, getLocale(null));
        return mf.format(a);
    }

    public String date(Date dte) {
        return VrUtils.getRelativeDateMessage(dte, getLocale(null));
    }

    public String date(Date dte, Locale loc) {
        return VrUtils.getRelativeDateMessage(dte, getLocale(null));
    }

    public String date(Date d, String format) {
        if (format.startsWith("#")) {
            return date(d, new Locale(format.substring(1)));
        }
        return d == null ? "" : new SimpleDateFormat(format).format(d);
    }

    public List<String> strsplit(String value, String chars) {
        return Arrays.asList(StringUtils.split(value, chars));
    }

    public String strcut(String value, int max) {
        return StringUtils.cut(value, max);
    }

    public String strexpand(String value, String chars, int min) {
        return StringUtils.expand(value, chars, min);
    }

    public String html2txt(String value) {
        return VrUtils.html2text(value);
    }

    public String html(String value) {
        return VrUtils.extractPureHTML(value);
    }

    public String lightHtml(Object value) {
        if (value == null) {
            return "";
        }
        String v = null;
        if (value instanceof ContentText) {
            v = ((ContentText) value).getContent();
        } else {
            v = String.valueOf(value).trim();
        }
        if (v.isEmpty()) {
            return "";
        }
        return VrUtils.extractPureHTML(replaceCustomURLs(v));
    }

    public String wiki2Html(String value) {
        return VrWikiParser.convertToHtml(replaceCustomWikiURLs(value), "Wiki");
    }

    public double percent(double val, double max) {
        return max == 0 ? 0 : ((val / max) * 100);
    }

    public double valOrPercent(boolean percent, double val, double max) {
        if (percent) {
            return max == 0 ? 0 : ((val / max) * 100);
        } else {
            return val;
        }
    }

    public Double nonNullValOrPercent(boolean percent, double val, double max) {
        if (percent) {
            return (max == 0 || val == 0) ? null : ((val / max) * 100);
        } else {
            return val == 0 ? null : val;
        }
    }

    public String valOrPercentString(boolean percent, double val, double max) {
        if (percent) {
            Double d = (max == 0 || val == 0) ? null : ((val / max) * 100);
            if (d != null) {
                return PERCENT_FORMAT.format(d) + "%";
            }
            return null;
        } else {
            if (val == 0) {
                return null;
            }
            if (Math.round(val) == val) {
                return String.valueOf((long) val);
            }
            return new DecimalFormat("0.0##").format(val);
        }
    }

    public String getFacesContextPrefix() {
        return VrWebHelper.getFacesContextPrefix();
    }

    public String getFacesContext() {
        return getContext() + "/" + getFacesContextPrefix();
    }

    public String getFullContext() {
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance == null) {
            return null;
        }
        HttpServletRequest req = (HttpServletRequest) currentInstance.getExternalContext().getRequest();
        StringBuilder sb = new StringBuilder();
        StringBuffer requestURL = req.getRequestURL();
        String contextPath = req.getContextPath();
        sb.append(requestURL.substring(0, requestURL.indexOf(contextPath) + contextPath.length()));
        return sb.toString();
    }

    public String getContext() {
        return VrWebHelper.getContext();
    }

    public String getCurrentRequestURI() {
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance == null) {
            return null;
        }
        HttpServletRequest req = (HttpServletRequest) currentInstance.getExternalContext().getRequest();
        return req.getRequestURI();
    }

    public String getCurrentPrintableRequestURI() {
        String u = getCurrentRequestURI();
        if (u.contains("?")) {
            u += "&";
        } else {
            u += "?";
        }
        u += "vr-layout=printable";
        return u;
    }

    public String getPublicThemeContext() {
        return VrWebHelper.getPublicThemeContext();
    }

    public String getPrivateThemeContext() {
        return VrWebHelper.getPrivateThemeContext();
    }

    public boolean isPrintableLayout() {
        return VrWebHelper.isPrintableLayout();
    }

    public String getPrivateTemplatePath() {
        return VrWebHelper.getPrivateTemplatePath();
    }

    public String getPublicThemePath() {
        return VrWebHelper.getPublicThemePath();
    }

    public String getPrivateThemePath() {
        return VrWebHelper.getPrivateThemePath();
    }

    // Session Aware
    public Locale getLocale(String preferred) {
        Locale loc = StringUtils.isEmpty(preferred) ? null : new Locale(preferred);
        if (loc == null) {
            UserToken s = null;
            try {
                s = CorePlugin.get().getCurrentToken();
            } catch (Exception e) {
                //ignore error
            }
            if (loc == null && s != null) {
                loc = s.getLocale() == null ? null : new Locale(s.getLocale());
            }
            if (loc == null) {
                loc = Locale.getDefault();
            }
        }
        return loc;
    }

    public String goBack() {
        return VrApp.getBean(VrMenuManager.class).goBack();
    }

    public String gotoHome() {
        return gotoPage("welcome", "");
    }

    public String gotoPage(String command, String arguments) {
        return VrApp.getBean(VrMenuManager.class).gotoPage(command, arguments);
    }

    public String gotoPageObjItem(String entity, String id) {
        return VrApp.getBean(VrMenuManager.class).gotoPageObjItem(entity, id);
    }

    public UserToken getCurrentToken() {
        return CorePlugin.get().getCurrentToken();
    }

    public String buildMenu() {
        return VrApp.getBean(VrMenuManager.class).buildMenu();
    }

    public VRMenuInfo getMenu() {
        return VrApp.getBean(VrMenuManager.class).getModel().getRoot();
    }

    public List<BreadcrumbItem> getBreadcrumb() {
        return VrApp.getBean(VrMenuManager.class).getModel().getBreadcrumb();
    }

    public BreadcrumbItem getTitleCrumb() {
        return VrApp.getBean(VrMenuManager.class).getModel().getTitleCrumb();
    }

    public String logout() {
        return VrApp.getBean(LoginCtrl.class).dologout();
    }

    public void notifyShutdown() {
        VrApp.getBean(AppGlobalCtrl.class).doNotifyShutdown();
    }

    public void cancelShutdown() {
        VrApp.getBean(AppGlobalCtrl.class).doCancelShutdown();
    }

    public boolean isShutdown() {
        return VrApp.getBean(AppGlobalCtrl.class).isShutdown();
    }

    public String getHeadMessageText() {
        return VrApp.getBean(AppGlobalCtrl.class).getHeadMessageText();
    }

    public String getHeadMessageStyle() {
        return VrApp.getBean(AppGlobalCtrl.class).getHeadMessageStyle();
    }

    public List<UserSessionInfo> getActiveSessions() {
        return VrApp.getBean(ActiveSessionsCtrl.class).getModel().getSessions();
    }

    public List<UserSessionInfo> getActiveSessionsByType(String type) {
        List<UserSessionInfo> r = new ArrayList<>();
        List<UserSessionInfo> activeSessions = getActiveSessions();
        if (activeSessions != null) {
            for (UserSessionInfo userSession : activeSessions) {
                String name = userSession.getUserTypeName();
                if (type == null || (type.equals(name))) {
                    r.add(userSession);
                }
            }
        }
        return r;
    }

    public Map<String, List<UserSessionInfo>> getActiveSessionsGroupedByType() {
        Map<String, List<UserSessionInfo>> r = new HashMap<>();
        List<UserSessionInfo> activeSessions = getActiveSessions();
        if (activeSessions != null) {
            for (UserSessionInfo userSession : activeSessions) {
                String name = (userSession != null && userSession.getUserId() != null && userSession.getUserTypeName() != null) ? (userSession.getUserTypeName()) : null;
                if (StringUtils.isEmpty(name)) {
                    name = "Autres";
                }
                List<UserSessionInfo> userSessions = r.get(name);
                if (userSessions == null) {
                    userSessions = new ArrayList<>();
                    r.put(name, userSessions);
                }
                userSessions.add(userSession);
            }
        }
        LinkedHashMap<String, List<UserSessionInfo>> r2 = new LinkedHashMap<>();
        for (String s : new TreeSet<String>(r.keySet())) {
            r2.put(s, r.get(s));
        }
        return r2;
    }

    public String getAppProperty(String name) {
        AppProperty property = core.getAppProperty(name, null);
        if (property != null) {
            return property.getPropertyValue();
        }
        return "$$" + name;
    }

    public int getPollInterval() {
        AppProperty property = core.getAppProperty("System.PollInterval", null);
        if (property != null) {
            Object appPropertyValue = core.getAppPropertyValue(property);
            if (appPropertyValue != null) {
                if (appPropertyValue instanceof Number) {
                    return ((Number) appPropertyValue).intValue();
                } else if (appPropertyValue instanceof String) {
                    try {
                        return Integer.parseInt(String.valueOf(appPropertyValue));
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            }
        }
        return 120;
    }

    public void onPoll() {
        core.onPoll();
    }

    public void error() throws Exception {
        throw new Exception("This is exception");
    }

    public StreamedContent downloadPath(String path) {
        return VrApp.getBean(DocumentsCtrl.class).downloadPath(path);
    }

    public CmsTextService getCmsTextService() {
        if (cmsTextService == null) {
            for (CmsTextService s : VrApp.getBeansForType(CmsTextService.class)) {
                return cmsTextService = s;
            }
            throw new IllegalArgumentException();
        }
        return cmsTextService;
    }

    public NotificationTextService getNotificationTextService() {
        if (notificationTextService == null) {
            for (NotificationTextService s : VrApp.getBeansForType(NotificationTextService.class)) {
                return notificationTextService = s;
            }
            throw new IllegalArgumentException();
        }
        return notificationTextService;
    }

    public TaskTextService getTaskTextService() {
        if (taskTextService == null) {
            for (TaskTextService s : VrApp.getBeansForType(TaskTextService.class)) {
                return taskTextService = s;
            }
            return taskTextService = new DummyTaskTextService();
        }
        return taskTextService;
    }

    public MessageTextService getMessageTextService() {
        if (messageTextService == null) {
            for (MessageTextService s : VrApp.getBeansForType(MessageTextService.class)) {
                return messageTextService = s;
            }
            return messageTextService = new DummyMessageTextService();
        }
        return messageTextService;
    }

    public String getCurrentUserPhoto() {
        return core.getCurrentUserPhoto();
    }

    public String getUnknownUserPhoto() {
        return core.getUnknownUserPhoto();
    }

    public String getUnknownUserIcon() {
        return core.getUnknownUserIcon();
    }

    public String getUserIcon(int id) {
        return core.getUserIcon(id);
    }

    public String getUserPhoto(int id) {
        return core.getUserPhoto(id);
    }

    public String getUserPhotoFullURL(int id) {
        String userPhoto = core.getUserPhoto(id);
        if (userPhoto == null) {
            return null;
        }
        return getAppWebPath(userPhoto);
    }

    public VFile getUserAbsoluteFile(int id, String path) {
        AppUser t = core.findUser(id);
        if (t != null) {
            VFile thisTeacherPhoto = core.getUserFolder(t.getLogin()).get(path);
            if (thisTeacherPhoto.exists()) {
                return thisTeacherPhoto;
            } else {
                //should by by user type!!
                VFile anyTeacherPhoto = core.getProfileFolder("Teacher").get(path);
                if (anyTeacherPhoto.exists()) {
                    return anyTeacherPhoto;
                }
            }
        }
        return null;
    }

    public String getAppWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return "/fs/" + virtualAbsolutePath;
    }

    public String getAbsoluteWebPath(String virtualAbsolutePath) {
        if (virtualAbsolutePath == null) {
            return null;
        }
        return Vr.get().getContext() + "/fs/" + virtualAbsolutePath;
    }

    public VFile getUserAbsoluteFile(int id, String... path) {
        return VrUtils.getUserAbsoluteFile(id, path);
    }

    public VFile[] getUserAbsoluteFiles(int id, String[] path) {
        return VrUtils.getUserAbsoluteFiles(id, path);
    }

    public List<StrLabel> extractLabels(String expr) {
        List<StrLabel> labels = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<labelName>\\w+)[:]((?<kindName>\\w+)[:])?(([\"](?<labelVal1>[^\"])[\"])|(?<labelVal2>[^ ]+))");
        Matcher m = pattern.matcher(expr);
        while (m.find()) {
            String labelName = m.group("labelName");
            String kindName = m.group("kindName");
            String labelVal1 = m.group("labelVal1");
            String labelVal2 = m.group("labelVal2");
            if (labelVal1 == null) {
                labelVal1 = labelVal2;
            }
            labels.add(new StrLabel(labelName, kindName == null ? "" : kindName, labelVal1));
        }
        return labels;
    }

    public String mapToken(String var, String defaultVal, String... fromTo) {
        var = trim(var);
        for (String s : var.split(" +")) {
            String v = mapValue(s, null, fromTo);
            if (v != null) {
                return v;
            }
        }
        return defaultVal;
    }

    public String mapValue(String var, String defaultVal, String... fromTo) {
        var = trim(var);
        for (int i = 0; i < fromTo.length; i += 2) {
            if (var.equals(fromTo[i])) {
                return fromTo[i + 1];
            }
        }
        return defaultVal;
    }

    public boolean allowed(String key) {
        return UPA.getPersistenceGroup().getSecurityManager().isAllowedKey(key);
    }

    public boolean hasProfile(String key) {
        return core.getCurrentToken().getProfileNames().contains(key);
    }

    public String gotoPublicSubSite(String siteFilter) {
        CorePlugin.get().getCurrentSession().setSelectedSiteFilter(siteFilter);
        return gotoPage("publicIndex", "");
    }

    public String getPathDirName(String path) {
        PathInfo p = getPathInfo(path);
        return p == null ? null : p.getDirName();
    }

    public String getPathBaseName(String path) {
        PathInfo p = getPathInfo(path);
        return p == null ? null : p.getBaseName();
    }

    public String getPathExtensionPart(String path) {
        PathInfo p = getPathInfo(path);
        return p == null ? null : p.getExtensionPart();
    }

    public String getPathNamePart(String path) {
        PathInfo p = getPathInfo(path);
        return p == null ? null : p.getNamePart();
    }

    public String getPathName(String path) {
        PathInfo p = getPathInfo(path);
        return p == null ? null : p.getPathName();
    }

    public PathInfo getPathInfo(String path) {
        if (path == null || path.isEmpty()) {
            return PathInfo.create("");
        }
        return PathInfo.create(path);
    }

    public String replaceCustomURLs(String format) {
        Pattern pattern = Pattern.compile("href=\"(?<url>[^\"]*)\"");
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(format);
        while (m.find()) {
            String url = m.group("url");
            int x = url.indexOf("://");
            String processed = null;
            if (x > 0) {
                String protocol = url.substring(0, x);
                String path = url.substring(protocol.length() + "://".length());
                processed = processCustomURL(protocol, path);
            }
            if (processed != null) {
                m.appendReplacement(sb, "href=\"" + processed + "\" target=\"_blank\"");
            } else {
                m.appendReplacement(sb, m.group());
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }

    public String replaceCustomWikiURLs(String format) {
        Pattern pattern = Pattern.compile("\\[\\[(?<url>[^\"]*)\\]\\]");
        StringBuffer sb = new StringBuffer();
        Matcher m = pattern.matcher(format);
        while (m.find()) {
            String url = m.group("url");
            int x = url.indexOf("://");
            String processed = null;
            if (x > 0) {
                String protocol = url.substring(0, x);
                String path = url.substring(protocol.length() + "://".length());
                processed = processCustomURL(protocol, path);
            }
            if (processed != null) {
                m.appendReplacement(sb, "[[" + processed + "]]");
            } else {
                m.appendReplacement(sb, m.group());
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private String processCustomURL(String protocol, String path) {
        if ("docs".equals(protocol)) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            path = path.replace('\'', '_');//fix injection issues
            return (getContext() + "/p/documents?a={path='" + path + "'}");
        }
        if ("pages".equals(protocol)) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return (getContext() + "/p" + path);
        }
        return null;
    }

    public CorePlugin getCore() {
        return core;
    }

    public List<String> autoCompleteProfileExpression(String query) {
        return core.autoCompleteProfileExpression(query);
    }

    public void updateProfileExpressionUsersCount(Object any) {
        System.out.println("Hi");
    }

    public String dblFormat(double d) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return String.format("%s", d);
        }
    }

    public String dblCustomFormat(double d, String format) {
        if (d == (long) d) {
            return String.format("%d", (long) d);
        } else {
            return getDecimalFormat(format).format(d);
        }
    }

    public DecimalFormat getDecimalFormat(String format) {
        DecimalFormat decimalFormat = decimalFormats.get(format);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(format);
            decimalFormats.put(format, decimalFormat);
        }
        return decimalFormat;
    }

    public boolean redirect(String path) throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance()
                .getExternalContext();
        if (ec != null) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.startsWith("/r/")) {
                //check if path is suffixed with xhtml
                int i = path.indexOf('?');
                if (i > 0) {
                    String substring = path.substring(0, i);
                    if (!substring.endsWith(".xhtml")) {
                        path = substring + ".xhtml" + path.substring(i);
                    }
                } else {
                    if (!path.endsWith(".xhtml")) {
                        path = path + ".xhtml";
                    }
                }
            }
            ec.redirect(ec.getRequestContextPath() + path);
        }
        return false;
    }

    public String strListifyNoEmpty(String sep, Object... items) {
        return StringUtils.listToStringDeep(sep, true, null, items);
    }

    public void postProcessDataExporterXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        HSSFCellStyle headerCellStyle = book.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        HSSFCreationHelper creationHelper = book.getCreationHelper();

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);

            cell.setCellStyle(headerCellStyle);
        }

        HSSFCellStyle intStyle = book.createCellStyle();
        intStyle.setDataFormat((short) 1);

        HSSFCellStyle decStyle = book.createCellStyle();
        decStyle.setDataFormat((short) 2);

        HSSFCellStyle dollarStyle = book.createCellStyle();
        dollarStyle.setDataFormat((short) 5);

        int maxColumn = -1;
        Map<String, HSSFCellStyle> datFormats = new HashMap<>();
        for (int rowInd = 1; rowInd < rowCount; rowInd++) {
            HSSFRow row = sheet.getRow(rowInd);
            int colCount = row.getPhysicalNumberOfCells();
            if (maxColumn < colCount) {
                maxColumn = colCount;
            }
            for (int cellInd = 0; cellInd < colCount; cellInd++) {
                HSSFCell cell = row.getCell(cellInd);

                String strVal = cell.getStringCellValue();

                if (strVal.startsWith("$")) {
                    //do nothing
                } else {
                    if (strVal.startsWith("'")) {
                        strVal = strVal.substring(1);
                    }
                    if (Utils.isDouble(strVal)) {
                        cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        if (Utils.isInteger(strVal)) {
                            int intVal = Integer.valueOf(strVal.trim());
                            cell.setCellStyle(intStyle);
                            cell.setCellValue(intVal);
                        } else if (Utils.isDouble(strVal)) {
                            double dblVal = Double.valueOf(strVal.trim());
                            cell.setCellStyle(decStyle);
                            cell.setCellValue(dblVal);
                        }
                    } else {
                        boolean isDate = false;
                        for (String dteFormat : new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "HH:mm"}) {
                            if (Utils.isDate(strVal, dteFormat)) {
                                HSSFCellStyle dateStyle = datFormats.get(dteFormat.trim());
                                if (dateStyle == null) {
                                    dateStyle = book.createCellStyle();
                                    dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat(dteFormat));
                                    datFormats.put(dteFormat, dateStyle);
                                }
                                cell.setCellStyle(dateStyle);
                                try {
                                    cell.setCellValue(new SimpleDateFormat(dteFormat).parse(strVal));
                                } catch (ParseException e) {
                                    //
                                }
                                isDate = true;
                                break;
                            }
                        }

                    }
                }
            }
        }
        if (maxColumn >= 0) {
            for (int cellInd = 0; cellInd < maxColumn; cellInd++) {
                sheet.autoSizeColumn(cellInd);
            }
        }

    }

    public String[] splitLabels(String string) {
        return StringUtils.removeDuplicates(StringUtils.split(string, ",; :"));
    }

    public List<SelectItem> entitySelectItems(String entityName, boolean selectNone, boolean selectNull) {
        //should cache this?
        List<SelectItem> list = new ArrayList<>();
        PersistenceUnit pu = UPA.getPersistenceUnit();
        Entity e = pu.getEntity(entityName);
        if (selectNone) {
            list.add(FacesUtils.createSelectItem("", "Non Spécifié"));
        }
        if (selectNull) {
            list.add(FacesUtils.createSelectItem(NULL_VALUE_STR, "--Valeur Nulle--"));
        }
        for (Object x : core.findAll(entityName)) {
            String name = e.getMainFieldValue(x);
            String id = VrUPAUtils.objToJson(x, e.getDataType()).toString();
            list.add(FacesUtils.createSelectItem(id, name));
        }
        return list;
    }

    public Converter entityObjConverter(String entityName) {
        return new EntityConverter(entityName);
    }

    public String fileExtensionPattern(String extensions) {
        HashSet<String> all = new HashSet<>();
        if (StringUtils.isEmpty(extensions)) {
            return "";
        }
        for (String s : extensions.split("[ ,|]")) {
            if (!StringUtils.isEmpty(s)) {
                all.add(s);
            }
        }
        if (all.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("/(\\.|\\/)(");

        String[] ext = all.toArray(new String[all.size()]);
        for (int i = 0; i < ext.length; i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(ext[i]);
        }
        sb.append(")$/");
        return sb.toString();
    }

    public void openUploadDialog(DocumentsUploadDialogCtrl.Config config, DocumentUploadListener listener) {
        VrApp.getBean(DocumentsUploadDialogCtrl.class).openCustomDialog(config, listener);
    }

    public String formatBytesSize(long size) {
        BytesSizeFormatter m = new BytesSizeFormatter();
        return m.format(size);
    }

    public void prepareCurrArticleType(ContentText listItem) {
        setHttpRequestAttribute("currArticleType",
                (isEmpty(listItem.getImageURL()) ? "center"
                : (this.contextTextHasDecoration(listItem, "left") ? "left"
                : this.contextTextHasDecoration(listItem, "right") ? "right"
                : this.contextTextHasDecoration(listItem, "center") ? "center"
                : this.randomize("left", "right")))
        );

        this.setHttpRequestAttribute("currArticleBackground",
                this.contextTextHasDecoration(listItem, "bg-rand-image") ? "rand-image"
                : this.contextTextHasDecoration(listItem, "bg-image") ? (this.isEmpty(listItem.getImageURL()) ? "rand-image" : "image")
                : this.contextTextHasDecoration(listItem, "bg-solid") ? "solid"
                : this.isEmpty(listItem.getImageURL()) ? this.randomize("solid", "solid")
                : "solid"
        );

        this.setHttpRequestAttribute("currArticleBackgroundImage",
                (this.getHttpRequest().getAttribute("currArticleBackground") == "rand-image")
                ? (this.randomizeList(this.listFlattenAndTrimAndAppend(this.strcat(this.getPublicThemeContext(), "/wplugins/crew/images/slide_5.jpg"), this.fsurlList(this.findValidImages("/Site/wplugins/crew/images/slider")))))
                : (this.getHttpRequest().getAttribute("currArticleBackground") == "image")
                ? (this.randomizeList(this.fsurlList(this.findValidImages(listItem.getImageURL()))))
                : ""
        );

        this.setHttpRequestAttribute("currArticleCompanionImage",
                (this.getHttpRequest().getAttribute("currArticleBackground") == "rand-image")
                ? this.url(listItem.getImageURL())
                : (this.getHttpRequest().getAttribute("currArticleBackground") == "image")
                ? (this.randomizeList(this.contentPathToFSUrlList(this.findImageAttachments(listItem.getImageAttachments()))))
                : this.url(listItem.getImageURL())
        );

        this.setHttpRequestAttribute("currArticleBackgroundSolid",
                (this.getHttpRequest().getAttribute("currArticleBackground") == "solid")
                ? this.randomize("#38569f", "#352f44", "#2f4432", "#44362f", "#442f39", "#38969f")
                : ""
        );
    }
}
