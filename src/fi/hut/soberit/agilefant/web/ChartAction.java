package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;

@Component("chartAction")
@Scope("prototype")
public class ChartAction extends ActionSupport {

    private static final long serialVersionUID = -2860355939047563512L;
    
    @Autowired
    private IterationBurndownBusiness iterationBurndownBusiness;
    @Autowired
    private IterationBusiness iterationBusiness;
    
    private int backlogId;
    
    private byte[] result;
    
    public void setIterationBurndownBusiness(
            IterationBurndownBusiness iterationBurndownBusiness) {
        this.iterationBurndownBusiness = iterationBurndownBusiness;
    }
    
    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }
    
    public String getIterationBurndown() {
        Iteration iteration = iterationBusiness.retrieve(backlogId);
        result = iterationBurndownBusiness.getIterationBurndown(iteration);
        return Action.SUCCESS;
    }

    public String getSmallIterationBurndown() {
        Iteration iteration = iterationBusiness.retrieve(backlogId);
        result = iterationBurndownBusiness.getSmallIterationBurndown(iteration);
        return Action.SUCCESS;
    }
    
    public InputStream getInputStream() {
        return new ByteArrayInputStream(result);
    }

    
    /* AUTOGENERATED */
    
    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public byte[] getResult() {
        return result;
    }    
}
