package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

/**
 * Action for listing backlogs contents.
 * 
 * Action contains caches for story responsibles, themes and spent effort
 * entries to ensure best performance compared to domain object lazy
 * fetching strategy.
 * 
 * ALL actions that list backlog contents should be derived from this class.
 * 
 * @author Pasi Pekkanen
 * 
 */
public class BacklogContentsAction extends ActionSupport {

    private static final long serialVersionUID = 3858289138593071787L;

    protected int backlogId;

    protected Backlog backlog;
    
    private List<Story> stories = new ArrayList<Story>();

//    private List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
//    
    private Map<Story, List<ResponsibleContainer>> backlogResponsibles = new HashMap<Story, List<ResponsibleContainer>>();
    
    private int storyPointSum;
    
//
//    private Map<BacklogItem, List<BusinessTheme>> backlogThemes = new HashMap<BacklogItem, List<BusinessTheme>>();
//
//    private Map<BacklogItem, TodoMetrics> backlogTodos = new HashMap<BacklogItem, TodoMetrics>();
//    
//    private AFTime spentEffortSum = new AFTime(0);
//    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Autowired
    protected BacklogBusiness backlogBusiness;
    
//    protected BusinessThemeBusiness businessThemeBusiness;
    
    @Autowired
    protected HourEntryBusiness hourEntryBusiness;
    
    @Autowired
    protected SettingBusiness settingBusiness;

    protected void initializeContents(int backlogId) {
        try {
            backlog = backlogBusiness.retrieve(backlogId);
        } catch (ObjectNotFoundException e) {
            return;
        }
        initializeContents(backlog);
    }

    protected void initializeContents(Backlog backlog) {
        if (backlog == null) {
            return;
        }
        
        stories = storyBusiness.getStoriesByBacklog(backlog);
        
//        backlogItems = backlogItemBusiness.getBacklogItemsByBacklog(backlog);
        backlogResponsibles = backlogBusiness.getResponsiblesByBacklog(backlog);
        storyPointSum = backlogBusiness.calculateStoryPointSum(backlog.getId());
//        backlogTodos = backlogItemBusiness.getTodosByBacklog(backlog);
//        backlogThemes = businessThemeBusiness.getBacklogItemBusinessThemesByBacklog(backlog);
//        
//        //calculate sum data
//        if(backlogItems != null) {
//            effortLeftSum = backlogBusiness.getEffortLeftSum(backlogItems);
//            origEstSum = backlogBusiness.getOriginalEstimateSum(backlogItems);
//            if(settingBusiness.isHourReportingEnabled()) {
//                spentEffortSum = backlogBusiness.getSpentEffortSum(backlogItems);
//            }
//        }
        
        // TODO: themes

    }
    
    protected void initializeContents() {
        initializeContents(backlog);
    }

    public void loadContents() {
        initializeContents(backlogId);
    }

    // AUTOGENERATED

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

//
//    public Map<BacklogItem, List<BusinessTheme>> getBacklogThemes() {
//        return backlogThemes;
//    }
//
//    public void setBacklogThemes(
//            Map<BacklogItem, List<BusinessTheme>> backlogThemes) {
//        this.backlogThemes = backlogThemes;
//    }
//
//    public Map<BacklogItem, TodoMetrics> getBacklogTodos() {
//        return backlogTodos;
//    }
//
//    public void setBacklogTodos(Map<BacklogItem, TodoMetrics> backlogTodos) {
//        this.backlogTodos = backlogTodos;
//    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

//    public List<BacklogItem> getBacklogItems() {
//        return backlogItems;
//    }
//
//    public void setBacklogItems(List<BacklogItem> backlogItems) {
//        this.backlogItems = backlogItems;
//    }
//
//    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
//        this.businessThemeBusiness = businessThemeBusiness;
//    }
//

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public Map<Story, List<ResponsibleContainer>> getBacklogResponsibles() {
        return backlogResponsibles;
    }
    
    public int getStoryPointSum() {
        return storyPointSum;
    }

}
