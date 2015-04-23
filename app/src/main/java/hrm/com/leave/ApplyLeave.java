package hrm.com.leave;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hrm.com.custom.enums.Leave;
import hrm.com.custom.enums.LeaveStatus;
import hrm.com.custom.fragment.DatePickerFragment;
import hrm.com.custom.fragment.SickLeaveAttachmentDialog;
import hrm.com.custom.listener.DatePickerListener;
import hrm.com.custom.listener.SickLeaveAttachmentListener;
import hrm.com.custom.listener.TaskListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Employee;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Role;
import hrm.com.model.Users;
import hrm.com.model.YearlyEntitlement;
import hrm.com.wrapper.GetLeaveRuleByRoleAndLeaveTypeWrapper;

@SuppressWarnings("ValidFragment")
public class ApplyLeave extends Fragment implements TaskListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    //FILE MANAGER
    final int THUMBSIZE = 128;

    private static final int REQUEST_SELECT_PICTURE = 7;
    private static final int REQUEST_IMAGE_CAPTURE = 8;

    private String selectedImagePath;

    private int selectedYearlyEntitlement = 0;
    private String leaveType;
    private YearlyEntitlement yearlyEntitlement = new YearlyEntitlement();
    private Date startDate;
    private Date endDate;
    private String reason;
    private Double numberOfDays;
    private Double yearlyBalance;
    //private AuditTrail auditTrail;
    private Double allowedMaximumLeaves;
    //private UploadedFile sickLeaveAttachment;

    private String leaveAttachmentName;
    private byte[] byteData = null;

    private String timings;
    private LeaveTransaction leaveTransaction = new LeaveTransaction();
    private List<String> roleList;
    private LeaveRuleBean leaveRuleBean;
    LeaveFlowDecisionsTaken leaveFlowDecisions;
    LeaveTransaction leavePersistBean;


    private List<YearlyEntitlement> yearlyEntitlementList;

    private Spinner editLeaveType;
    private EditText editEntitlement;
    private EditText editCurrentLeaveBal;
    private EditText editYearlyLeaveBal;
    private EditText editStartDate;
    private EditText editEndDate;
    private EditText editNoOfDays;
    private EditText editReason;
    private EditText editAttachment;

    private TextView txtEntitlement, txtCurrentLeaveBal, txtYearlyLeaveBal, txtAttachment;

    private ImageView thumbnail;
    /*
    private TableRow rowCurrentLeaveBal;
    private TableRow rowYearlyLeaveBal;
    private TableRow rowEntitlement;
*/
    private Button btnApplyLeave, btnAttachment;

    private String username;
    private String password;
    private Employee employee;
    private Users user;
    private int employeeId;

    public static ApplyLeave newInstance() {
        return new ApplyLeave();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_apply_leave, container, false);
        initLayout(rootView);


        this.username = ((HomeActivity) getActivity()).getUsername();
        this.password = ((HomeActivity) getActivity()).getPassword();
        this.employee = ((HomeActivity) getActivity()).getActiveEmployee();
        this.user = (((HomeActivity) getActivity()).getActiveUser());
        this.employeeId = employee.getId();

        PopulateYearlyEntitlementTask pop = new PopulateYearlyEntitlementTask();
        pop.execute();
        return rootView;
    }

    public void initLayout(final View rootView) {
        yearlyEntitlementList = new ArrayList<YearlyEntitlement>();

        editLeaveType = (Spinner) rootView.findViewById(R.id.editLeaveType);
        editLeaveType.setOnItemSelectedListener(this);/*

        rowEntitlement = (TableRow) rootView.findViewById(R.id.rowEntitlement);
        rowCurrentLeaveBal = (TableRow) rootView.findViewById(R.id.rowCurrentLeaveBal);
        rowYearlyLeaveBal = (TableRow) rootView.findViewById(R.id.rowYearlyLeaveBal);
        rowCurrentLeaveBal = (TableRow) rootView.findViewById(R.id.rowCurrentLeaveBal);
        rowCurrentLeaveBal = (TableRow) rootView.findViewById(R.id.rowCurrentLeaveBal);
*/
        txtEntitlement = (TextView) rootView.findViewById(R.id.entitlement);
        txtCurrentLeaveBal = (TextView) rootView.findViewById(R.id.currentLeaveBal);
        txtYearlyLeaveBal = (TextView) rootView.findViewById(R.id.yearlyLeaveBal);
        txtAttachment = (TextView) rootView.findViewById(R.id.attachment);

        editEntitlement = (EditText) rootView.findViewById(R.id.editEntitlement);
        editCurrentLeaveBal = (EditText) rootView.findViewById(R.id.editCurrentLeaveBal);
        editYearlyLeaveBal = (EditText) rootView.findViewById(R.id.editYearlyLeaveBal);
        editStartDate = (EditText) rootView.findViewById(R.id.editStartDate);
        editEndDate = (EditText) rootView.findViewById(R.id.editEndDate);
        editNoOfDays = (EditText) rootView.findViewById(R.id.editNoOfDays);
        editReason = (EditText) rootView.findViewById(R.id.editReason);
        editAttachment = (EditText) rootView.findViewById(R.id.editAttachment);

        btnApplyLeave = (Button) rootView.findViewById(R.id.btnApplyLeave);
        btnAttachment = (Button) rootView.findViewById(R.id.btnAttachment);

        thumbnail = (ImageView) rootView.findViewById(R.id.thumbnailAttachment);

        editStartDate.setOnClickListener(this);
        editEndDate.setOnClickListener(this);
        btnAttachment.setOnClickListener(this);
        btnApplyLeave.setOnClickListener(this);
    }

    @Override
    public void onTaskCompleted() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.editLeaveType:
                Toast.makeText(getActivity().getApplicationContext(), String.valueOf(yearlyEntitlementList.get(position).getId()), Toast.LENGTH_SHORT).show();
                updateUiValues(position);
                yearlyEntitlementSelected(yearlyEntitlementList.get(position).getId());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.editStartDate:
            case R.id.editEndDate:
                final TextView activeTextView;

                if (v.getId() == R.id.editStartDate)
                    activeTextView = editStartDate;
                else
                    activeTextView = editEndDate;

                DatePickerFragment dialog = new DatePickerFragment(new DatePickerListener() {
                    @Override
                    public void onDateSelected(String date) {
                        activeTextView.setText(date);
                    }
                });

                dialog.show(getActivity().getSupportFragmentManager(), "MyDatePickerDialog");
                break;

            case R.id.btnAttachment:
                SickLeaveAttachmentDialog sickDialog = new SickLeaveAttachmentDialog(new SickLeaveAttachmentListener() {
                    @Override
                    public void onCameraSelected() {
                        dispatchCameraIntent();
                    }

                    @Override
                    public void onGallerySelected() {
                        dispatchGalleryIntent();
                    }
                });
                sickDialog.show(getActivity().getSupportFragmentManager(), "SickLeaveDialog");
                break;
            case R.id.btnApplyLeave:
                Toast.makeText(getActivity().getApplicationContext(), "Apply Leave", Toast.LENGTH_SHORT).show();
                try {
                    setValues();
                    applyLeave();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Parse Exception", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap;

        //Select picture from gallery
        if (requestCode == REQUEST_SELECT_PICTURE && resultCode == getActivity().RESULT_OK){
            Toast.makeText(getActivity().getApplicationContext(), "File uploaded from gallery", Toast.LENGTH_SHORT).show();
            Uri selectedImageUri = data.getData();
            /* OI FILE MANAGER DISABLED
            //OI FILE Manager
            filemanagerstring = selectedImageUri.getPath();
            */
            //MEDIA GALLERY
            selectedImagePath = getPath(selectedImageUri);

        }

        //Take picture from camera
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity().getApplicationContext(), "File uploaded from camera", Toast.LENGTH_SHORT).show();
        }

        if (selectedImagePath != null) {
            imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), THUMBSIZE, THUMBSIZE);
            retrieveFile(selectedImagePath, imageBitmap);

        }

    }

    //Gallery functions
    public void dispatchGalleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), REQUEST_SELECT_PICTURE);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            //NULL POINTER IF OI FILE MANAGER USED FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    //Camera functions
    private void dispatchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getActivity().getApplicationContext(), "Error Creating File", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                makeAvailableToGallery();
            }
        }
    }

    private void makeAvailableToGallery(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(selectedImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

       selectedImagePath = image.getAbsolutePath();
        return image;
    }

    public void retrieveFile(String filepath, Bitmap imageBitmap){
        File imagefile = new File(filepath);
        leaveAttachmentName = imagefile.getName();
        editAttachment.setText(leaveAttachmentName);
        thumbnail.setImageBitmap(imageBitmap);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imagefile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
        byteData = baos.toByteArray();

        //String encImage = Base64.encodeToString(b, Base64.DEFAULT);

    }

    public void updateUiValues(int position) {
        if (!(yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Unpaid leave") || yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Time-In-Lieu leave"))) {
            editEntitlement.setVisibility(View.VISIBLE);
            txtEntitlement.setVisibility(View.VISIBLE);
            editEntitlement.setText(String.valueOf(yearlyEntitlementList.get(position).getEntitlement()));
        } else {
            editEntitlement.setVisibility(View.GONE);
            txtEntitlement.setVisibility(View.GONE);
        }

        if (yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Annual leave")) {
            editCurrentLeaveBal.setVisibility(View.VISIBLE);
            txtCurrentLeaveBal.setVisibility(View.VISIBLE);
            editCurrentLeaveBal.setText(String.valueOf(yearlyEntitlementList.get(position).getCurrentLeaveBalance()));
        } else {
            editCurrentLeaveBal.setVisibility(View.GONE);
            txtCurrentLeaveBal.setVisibility(View.GONE);
        }

        if (!(yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Unpaid leave") || yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Time-In-Lieu leave"))) {
            editYearlyLeaveBal.setVisibility(View.VISIBLE);
            txtYearlyLeaveBal.setVisibility(View.VISIBLE);
            editYearlyLeaveBal.setText(String.valueOf(yearlyEntitlementList.get(position).getYearlyLeaveBalance()));
        } else {
            editYearlyLeaveBal.setVisibility(View.GONE);
            txtYearlyLeaveBal.setVisibility(View.GONE);
        }

        if (yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Sick leave")) {
            txtAttachment.setVisibility(View.VISIBLE);
            editAttachment.setVisibility(View.VISIBLE);
            btnAttachment.setVisibility(View.VISIBLE);
        } else{
            txtAttachment.setVisibility(View.GONE);
            editAttachment.setVisibility(View.GONE);
            btnAttachment.setVisibility(View.GONE);
        }
    }

    public Double getAllowedMaximumLeaves() {
        return allowedMaximumLeaves;
    }
    public void setAllowedMaximumLeaves(Double allowedMaximumLeaves) {
        this.allowedMaximumLeaves = allowedMaximumLeaves;
    }
    public int getSelectedYearlyEntitlement() {
        return selectedYearlyEntitlement;
    }
    public void setSelectedYearlyEntitlement(int selectedYearlyEntitlement) {
        this.selectedYearlyEntitlement = selectedYearlyEntitlement;
    }

    public void yearlyEntitlementSelected(int position) {
        setSelectedYearlyEntitlement(position);
        findYearlyEntitlement();

        /*
 RequestContext.getCurrentInstance().addCallbackParam("currentBalance", yearlyEntitlement.getCurrentLeaveBalance());
        RequestContext.getCurrentInstance().addCallbackParam("leaveType", leaveType);
        RequestContext.getCurrentInstance().addCallbackParam("isOneYearOver", isEmployeeFinishedOneYear());*/

    }

    private void findYearlyEntitlement() {
        YearlyEntitlementFindOneTask findOne = new YearlyEntitlementFindOneTask();
        findOne.execute();
    }
    public YearlyEntitlement getYearlyEntitlement() {
        return yearlyEntitlement;
    }
    public void setYearlyEntitlement(YearlyEntitlement yearlyEntitlement) {
        this.yearlyEntitlement = yearlyEntitlement;
    }

    public Double getYearlyBalance() {
        return yearlyBalance;
    }
    public void setYearlyBalance(Double yearlyBalance) {
        this.yearlyBalance = yearlyBalance;
    }
    public String getLeaveType() {
        return leaveType;
    }
    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    private boolean isEmployeeFinishedOneYear() {

        if (employee != null) {
            Calendar joinDate = Calendar.getInstance();
            joinDate.setTime(employee.getJoinDate());
            Calendar today = Calendar.getInstance();
            int curYear = today.get(Calendar.YEAR);
            int curMonth = today.get(Calendar.MONTH);
            int curDay = today.get(Calendar.DAY_OF_MONTH);

            int year = joinDate.get(Calendar.YEAR);
            int month = joinDate.get(Calendar.MONTH);
            int day = joinDate.get(Calendar.DAY_OF_MONTH);

            int age = curYear - year;
            if (curMonth < month || (month == curMonth && curDay < day))
                age--;

            if (age >= 1)
                return true;
        }

        return false;
    }


    private Double findAnnualYearlyEntitlement(int employeeId){
        try{
            YearlyEntitlementFindAnnualTask findAnnualTask = new YearlyEntitlementFindAnnualTask();
            findAnnualTask.execute();
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return	yearlyEntitlement.getYearlyLeaveBalance();
    }




    public String getTimings() {
        return timings;
    }
    public void setTimings(String timings) {
        this.timings = timings;
    }

    public void checkHalfDayLeave(Long days){
        if(days==0.5){
            numberOfDays=0.5;
        }
    }

    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public Double getNumberOfDays() {
        return numberOfDays;
    }
    public void setNumberOfDays(Double numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setValues() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date;

        date = sdf.parse(editStartDate.getText().toString());
        setStartDate(date);

        date = sdf.parse(editEndDate.getText().toString());
        setEndDate(date);

        setNumberOfDays(Double.valueOf(editNoOfDays.getText().toString()));
        setReason(editReason.getText().toString());

    }



    public String applyLeave() /*throws LeaveApplicationException,RoleNotFound */{
        // Validating whether number of days is half day or full day or not
        // valid

        if (getNumberOfDays() != null) {
            double x = getNumberOfDays().doubleValue() - (long) getNumberOfDays().doubleValue();
            if (!(x == 0.0 || x == 0.5)) {

                // FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.days.validation"), "Leave error message");
                // msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                // FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        }
/*
        if (isEmployeeFinishedOneYear()) {

            //check if number of days applied is more than yearlyLeaveBalance
            if (!(Leave.UNPAID.equalsName(leaveType) || Leave.TIMEINLIEU.equalsName(leaveType))
                    && !(numberOfDays <= yearlyEntitlement.getYearlyLeaveBalance())) {
                //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.sick.validation"),"Leave error message");
                //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                //FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        } else {
            // validating applied leaves is in the range of current balance -
            // applied leaves > = -3
            if ("Annual".equalsIgnoreCase(leaveType) && StringUtils.isNotBlank(leaveType) && StringUtils.isNotEmpty(leaveType)) {

                if (!(yearlyEntitlement.getCurrentLeaveBalance() - numberOfDays >= -3)) {
                    //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.sick.validation"), "Leave error message");
                    //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    //FacesContext.getCurrentInstance().addMessage(null, msg);
                    return "";
                }
            } else if (!("Unpaid".equalsIgnoreCase(leaveType) || Leave.TIMEINLIEU.equalsName(leaveType))
                    && StringUtils.isNotBlank(leaveType) && StringUtils.isNotEmpty(leaveType) && numberOfDays > yearlyEntitlement.getYearlyLeaveBalance()) {
                //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.sick.validation"), "Leave error message");
                //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                //FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        }*/
        if (numberOfDays < 0.5) {
            //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.applyleave.numberofdays"), "Leave error message");
            //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            //FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }

        // checking unpaid leaves allowing only maximum 30 days.
        if (Leave.UNPAID.equalsName(leaveType) && numberOfDays > 30) {
            //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.unpaid.validation"), "Leave error message");
            //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            //FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }

        //check if startdate after enddate
        if (startDate.after(endDate)) {
            //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.applyleave.datesRange"), "Leave error message.");
            //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            //FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        } else {

            leaveTransaction.setApplicationDate(new Date());
            leaveTransaction.setDeleted(false);
            leaveTransaction.setEmployee(getEmployee());
            leaveTransaction.setLeaveType(getYearlyEntitlement().getLeaveType());
            leaveTransaction.setNumberOfDays(getNumberOfDays());

            if (!Leave.TIMEINLIEU.equalsName(leaveTransaction.getLeaveType().getName()))
                leaveTransaction.setYearlyLeaveBalance(getYearlyEntitlement().getYearlyLeaveBalance());
            else
                leaveTransaction.setYearlyLeaveBalance(findAnnualYearlyEntitlement(employee.getId()));

            leaveTransaction.setReason(getReason());
            leaveTransaction.setStartDateTime(getStartDate());
            leaveTransaction.setEndDateTime(getEndDate());


            if (getNumberOfDays().doubleValue()	- (long) getNumberOfDays().doubleValue() == 0.5)
                leaveTransaction.setTimings(getTimings());
            else
                leaveTransaction.setTimings(null);

            leaveTransaction.setCreatedBy(getUser().getUsername());
            leaveTransaction.setCreationTime(new Date());
            leaveTransaction.setStatus(LeaveStatus.PENDING.toString());

            if ("Sick".equalsIgnoreCase(leaveType)) {
                if (byteData != null) {
                    leaveTransaction.setSickLeaveAttachment(byteData);
                    leaveTransaction.setSickLeaveAttachmentName(leaveAttachmentName);
                } else {
                    //FacesMessage msg = new FacesMessage(getExcptnMesProperty("error.sickleaveattachment.mcNotFound"), getExcptnMesProperty("error.sickleaveattachment.mcNotFound"));
                    //msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                    //FacesContext.getCurrentInstance().addMessage(null, msg);
                    return "";
                }
            }

            // Get the Leave Rule for the applying leave applicant
            roleList = new ArrayList<String>();
            for (Role role : getUser().getUserRoles()) {
                roleList.add(role.getRole().trim());
            }

            GetLeaveRuleByRoleAndLeaveTypeTask getLeaveRuleByRoleAndLeaveTypeTask = new GetLeaveRuleByRoleAndLeaveTypeTask();
            getLeaveRuleByRoleAndLeaveTypeTask.execute();

        }

        //TO BE CHANGED
        //return "/protected/index.jsf?faces-redirect=true";
        return "";
    }

    public Users getUser() {
        return user;
    }
    public void setUser(Users user) {
        this.user = user;
    }

    private class PopulateYearlyEntitlementTask extends AsyncTask<String, Void, List<YearlyEntitlement>> {

        @Override
        protected void onPostExecute(List<YearlyEntitlement> result) {
            super.onPostExecute(result);

            yearlyEntitlementList = result;
            // Toast.makeText(getActivity().getApplicationContext(), leaveTypeList.size(), Toast.LENGTH_SHORT).show();

            ArrayAdapter<YearlyEntitlement> spinnerAdapter = new ArrayAdapter<YearlyEntitlement>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, yearlyEntitlementList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editLeaveType.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<YearlyEntitlement> doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/yearlyEntitlement/findYearlyEntitlementListByEmployee?employeeId={employeeId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<YearlyEntitlement[]> response = restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement[].class, employeeId);
            YearlyEntitlement[] yearlyEntArray = response.getBody();
            List<YearlyEntitlement> result = Arrays.asList(yearlyEntArray);
            return result;
        }
    }

    private class YearlyEntitlementFindOneTask extends AsyncTask<String, Void, hrm.com.model.YearlyEntitlement>{
        @Override
        protected void onPostExecute(YearlyEntitlement result) {
            super.onPostExecute(result);
            yearlyEntitlement = result;

            if(getYearlyEntitlement() != null) {
                allowedMaximumLeaves=0.0;
                setLeaveType(getYearlyEntitlement().getLeaveType().getName());
                setYearlyBalance(getYearlyEntitlement().getYearlyLeaveBalance());
                if(isEmployeeFinishedOneYear()){
                    if(Leave.UNPAID.equalsName(leaveType))
                        allowedMaximumLeaves=30.0;
                    else
                        allowedMaximumLeaves=getYearlyEntitlement().getYearlyLeaveBalance();
                }
                else{
                    if(Leave.ANNUAL.equalsName(leaveType))
                        allowedMaximumLeaves = getYearlyEntitlement().getCurrentLeaveBalance()+3.0;
                    else if(Leave.UNPAID.equalsName(leaveType))
                        allowedMaximumLeaves=30.0;
                    else
                        allowedMaximumLeaves=getYearlyEntitlement().getYearlyLeaveBalance();
                }
            }

        }

        @Override
        protected YearlyEntitlement doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/yearlyEntitlement/findOne?yearlyEntitlementId={selectedYearlyEntitlement}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<YearlyEntitlement> response = restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement.class, selectedYearlyEntitlement);
            return response.getBody();
        }


    }

    private class YearlyEntitlementFindAnnualTask extends AsyncTask<String, Void, hrm.com.model.YearlyEntitlement>{
        @Override
        protected void onPostExecute(YearlyEntitlement result) {
            super.onPostExecute(result);
            yearlyEntitlement = result;

        }

        @Override
        protected YearlyEntitlement doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/yearlyEntitlement/findAnnualYearlyEntitlementOfEmployee?employeeId={employeeId}";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<String> request = new HttpEntity<String>(headers);
            ResponseEntity<YearlyEntitlement> response = restTemplate.exchange(url, HttpMethod.GET, request, YearlyEntitlement.class, employeeId);
            return response.getBody();
        }



    }

    private class ProcessAppliedLeaveOfEmployeeTask  extends AsyncTask<String, Void, LeaveTransaction>{

        @Override
        protected void onPostExecute(LeaveTransaction result) {
            super.onPostExecute(result);
            leavePersistBean = result;

            setSelectedYearlyEntitlement(0);
            setLeaveType("");
            setStartDate(null);
            setEndDate(null);
            setReason("");

        }

        @Override
        protected LeaveTransaction doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/processAppliedLeaveOfEmployee";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(leaveTransaction, headers);
            ResponseEntity<LeaveTransaction> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveTransaction.class);
            return response.getBody();
        }



    }

    private class GetLeaveRuleByRoleAndLeaveTypeTask  extends AsyncTask<String, Void, LeaveRuleBean>{

        GetLeaveRuleByRoleAndLeaveTypeWrapper leaveRuleWrapper;

        @Override
        protected void onPostExecute(LeaveRuleBean result) {
            super.onPostExecute(result);
            leaveTransaction.setLeaveRuleBean(result);
            leaveRuleBean = result;
            SaveLeaveApprovalDecisionsTask saveLeaveApprovalDecisionsTask = new SaveLeaveApprovalDecisionsTask();
            saveLeaveApprovalDecisionsTask.execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            leaveRuleWrapper = new GetLeaveRuleByRoleAndLeaveTypeWrapper();

            leaveRuleWrapper.setLeaveType(leaveType);
            leaveRuleWrapper.setRoleType(roleList);
        }

        @Override
        protected LeaveRuleBean doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/getLeaveRuleByRoleAndLeaveType";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(leaveRuleWrapper, headers);
            ResponseEntity<LeaveRuleBean> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveRuleBean.class);
            return response.getBody();
        }



    }

    private class SaveLeaveApprovalDecisionsTask  extends AsyncTask<String, Void, LeaveFlowDecisionsTaken>{
        @Override
        protected void onPostExecute(LeaveFlowDecisionsTaken result) {
            super.onPostExecute(result);
            leaveFlowDecisions = result;
            leaveTransaction.setDecisionsBean(leaveFlowDecisions);
            leaveTransaction.setDecisionToBeTaken(leaveRuleBean.getApproverNameLevel1());
            ProcessAppliedLeaveOfEmployeeTask process = new ProcessAppliedLeaveOfEmployeeTask();
            process.execute();

        }

        @Override
        protected LeaveFlowDecisionsTaken doInBackground(String... params) {
            // The connection URL
            String url = "http://10.0.2.2:8080/restWS-0.0.1-SNAPSHOT/protected/leaveTransaction/saveLeaveApprovalDecisions";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            String plainCreds = username + ":" + password;
            String base64EncodedCredentials = Base64.encodeToString(plainCreds.getBytes(), Base64.NO_WRAP);
            headers.add("Authorization", "Basic " + base64EncodedCredentials);

            // Add the String message converter
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity request = new HttpEntity(null, headers);
            ResponseEntity<LeaveFlowDecisionsTaken> response = restTemplate.exchange(url, HttpMethod.POST, request, LeaveFlowDecisionsTaken.class);
            return response.getBody();
        }



    }
}
