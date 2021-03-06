package hrm.com.webservice;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import hrm.com.custom.rest.CustomRestTemplate;
import hrm.com.hrmprototype.App;
import hrm.com.hrmprototype.R;
import hrm.com.model.YearlyEntitlement;

/**
 * Created by Beans on 5/5/2015.
 */
public class YearlyEntitlementWS {

    RestTemplate restTemplate;
    String username;

    CustomRestTemplate customRT;

    public YearlyEntitlementWS(String username, String password) {
        this.username = username;
        customRT = new CustomRestTemplate(username, password);
        restTemplate = customRT.getRestTemplate();
    }

    public YearlyEntitlement findAnnualYearlyEntitlementOfEmployee(int employeeId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/yearlyEntitlement/findAnnualYearlyEntitlementOfEmployee?employeeId={employeeId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement.class, employeeId).getBody();
    }

    public List<YearlyEntitlement> findYearlyEntitlementListByEmployee(int employeeId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/yearlyEntitlement/findYearlyEntitlementListByEmployee?employeeId={employeeId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        ResponseEntity<YearlyEntitlement[]> response = restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement[].class, employeeId);
        YearlyEntitlement[] yearlyEntArray = response.getBody();
        return Arrays.asList(yearlyEntArray);
    }

    public YearlyEntitlement findOne(int selectedYearlyEntitlement){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/yearlyEntitlement/findOne?yearlyEntitlementId={selectedYearlyEntitlement}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement.class, selectedYearlyEntitlement).getBody();

    }

    public YearlyEntitlement findByEmployeeAndLeaveType(int employeeId, int leaveTypeId){
        String url = App.getContext().getResources().getString(R.string.host_address) + "/protected/yearlyEntitlement/findByEmployeeAndLeaveType?employeeId={employeeId}&leaveTypeId={leaveTypeId}";
        HttpEntity<String> request = new HttpEntity<String>(customRT.getHeaders());

        return restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement.class, employeeId, leaveTypeId).getBody();

    }

}
