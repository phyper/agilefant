package fi.hut.soberit.agilefant.web;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationToken;
import org.springframework.stereotype.Component;

import com.opensymphony.webwork.interceptor.PrincipalAware;
import com.opensymphony.webwork.interceptor.PrincipalProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.business.TimesheetExportBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.AgilefantUserDetails;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import flexjson.JSONSerializer;

/**
 * 
 * @author Vesa Pirila / Spider
 * @author Pasi Pekkanen
 *
 */
@Component("timesheetAction")
@Scope("prototype")
public class TimesheetAction extends ActionSupport implements PrincipalAware {

    private static final long serialVersionUID = -8988740967426943267L;
    
    @Autowired
    private TimesheetBusiness timesheetBusiness;
    
    @Autowired
    private TimesheetExportBusiness timesheetExportBusiness;

    @Autowired
    private UserBusiness userBusiness;
    
    private Set<Integer> productIds = new HashSet<Integer>();
    
    private Set<Integer> projectIds = new HashSet<Integer>();
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private List<BacklogTimesheetNode> products;
    
    private String startDate;

    private String endDate;
    
    private String interval;
    
    private Set<Integer> userIds = new HashSet<Integer>();
    
    private int currentUserId = 0;
    
    private boolean onlyOngoing = true;
    
    private long effortSum = 0;
    
    private ByteArrayOutputStream exportableReport;
   
    
    
    /**
     * Needed for xwork's execAndWait as action is executed in a different
     * thread than the wait page. Thus no static threadLocal based principals
     * (such as those in SecurityUtil) can be used.
     */
    public void setPrincipalProxy(PrincipalProxy principalProxy) {
        Principal principal = principalProxy.getUserPrincipal();
        AgilefantUserDetails ud;
        if (principal instanceof RememberMeAuthenticationToken) {
            ud = (AgilefantUserDetails) ((RememberMeAuthenticationToken) principal)
                    .getPrincipal();
        } else {
            ud = (AgilefantUserDetails) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }
        currentUserId = ud.getUserId();
        
    }
    /*
     * -1 in product, project or iteration id array remarks "select all" option
     */
    public Set<Integer> getSelectedBacklogs() {
        Set<Integer> ret = new HashSet<Integer>();
        if(this.projectIds.contains(-1)) {
            if(this.onlyOngoing) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.productIds);
            }
        } else if(this.iterationIds.contains(-1)) {
             if(this.onlyOngoing) {
                ret.addAll(this.iterationIds);
            } else {
                ret.addAll(this.projectIds);
            }
        } else {
            if(this.projectIds.size() == 0) {
                ret.addAll(this.productIds);
            } else if(this.iterationIds.size() == 0) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.iterationIds);
            }
        }
        ret.remove(-1);
        return ret;
    }
    public String initialize() {
        this.interval = "TODAY";
        this.onlyOngoing = false;
        //this.userIds.add(this.currentUserId);
        return Action.SUCCESS;
    }
    private DateTime getDateTimeFromString(String val) {
        try {
            Date dateVal = CalendarUtils.parseDateFromString(val);
            return new DateTime(dateVal.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
    public String generateTree(){
        Set<Integer> selectedBacklogIds = this.getSelectedBacklogs();
        if(selectedBacklogIds == null || selectedBacklogIds.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }        
        products = timesheetBusiness.getRootNodes(selectedBacklogIds, getDateTimeFromString(startDate), getDateTimeFromString(endDate), this.userIds);
        effortSum = timesheetBusiness.getRootNodeSum(products);
        return Action.SUCCESS;
    }
    
    public String generateExeclReport(){
        Set<Integer> selectedBacklogIds = this.getSelectedBacklogs();
        if(selectedBacklogIds == null || selectedBacklogIds.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }        
        Workbook wb = this.timesheetExportBusiness.generateTimesheet(this, selectedBacklogIds, getDateTimeFromString(startDate), getDateTimeFromString(endDate), userIds);
        this.exportableReport = new ByteArrayOutputStream();
        try {
            wb.write(this.exportableReport);
        } catch (IOException e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public List<User> getSelectedUsers() {
        if(this.userIds == null) {
            return Collections.emptyList();
        }
        List<User> selectedUsers = new ArrayList<User>();
        for(int userId : this.getUserIds()) {
            User user = this.userBusiness.retrieve(userId);
            if(user != null) {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
    }
    public TimesheetBusiness getTimesheetBusiness() {
        return timesheetBusiness;
    }

    public void setTimesheetBusiness(TimesheetBusiness timesheetBusiness) {
        this.timesheetBusiness = timesheetBusiness;
    }

    public Set<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Integer> productIds) {
        this.productIds = productIds;
    }

    public Set<Integer> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Integer> projectIds) {
        this.projectIds = projectIds;
    }

    public Set<Integer> getIterationIds() {
        return iterationIds;
    }

    public void setIterationIds(Set<Integer> iterationIds) {
        this.iterationIds = iterationIds;
    }

    public List<BacklogTimesheetNode> getProducts() {
        return products;
    }

    public void setProducts(List<BacklogTimesheetNode> products) {
        this.products = products;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public boolean isOnlyOngoing() {
        return onlyOngoing;
    }

    public void setOnlyOngoing(boolean onlyOngoing) {
        this.onlyOngoing = onlyOngoing;
    }
    
    public String getJSONProducts() {
        return new JSONSerializer().serialize(this.productIds);
    }
    public String getJSONProjects() {
        return new JSONSerializer().serialize(this.projectIds);
    }
    public String getJSONIterations() {
        return new JSONSerializer().serialize(this.iterationIds);
    }
    public long getEffortSum() {
        return effortSum;
    }
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
    public void setTimesheetExportBusiness(
            TimesheetExportBusiness timesheetExportBusiness) {
        this.timesheetExportBusiness = timesheetExportBusiness;
    }
    public InputStream getSheetData() {
        return new ByteArrayInputStream(this.exportableReport.toByteArray());
    }
    public void setExportableReport(ByteArrayOutputStream exportableReport) {
        this.exportableReport = exportableReport;
    }
    
}
