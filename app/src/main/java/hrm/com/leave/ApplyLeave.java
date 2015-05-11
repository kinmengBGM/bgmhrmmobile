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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hrm.com.custom.enums.Leave;
import hrm.com.custom.enums.LeaveStatus;
import hrm.com.custom.fragment.DatePickerFragment;
import hrm.com.custom.fragment.SickLeaveAttachmentDialog;
import hrm.com.custom.listener.DatePickerListener;
import hrm.com.custom.listener.SickLeaveAttachmentListener;
import hrm.com.hrmprototype.HomeActivity;
import hrm.com.hrmprototype.R;
import hrm.com.model.Employee;
import hrm.com.model.LeaveFlowDecisionsTaken;
import hrm.com.model.LeaveRuleBean;
import hrm.com.model.LeaveTransaction;
import hrm.com.model.Role;
import hrm.com.model.Users;
import hrm.com.model.YearlyEntitlement;
import hrm.com.webservice.LeaveApplicationEmailNotificationWS;
import hrm.com.webservice.LeaveTransactionWS;
import hrm.com.webservice.YearlyEntitlementWS;
import hrm.com.wrapper.GetLeaveRuleByRoleAndLeaveTypeWrapper;

@SuppressWarnings("ValidFragment")
public class ApplyLeave extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

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
    private EditText editEntitlement, editCurrentLeaveBal, editYearlyLeaveBal, editStartDate, editEndDate, editNoOfDays, editReason, editAttachment;
    private TextView txtEntitlement, txtCurrentLeaveBal, txtYearlyLeaveBal, txtAttachment;
    private ImageView thumbnail;
    private Button btnApplyLeave;
    private ImageButton btnAttachment;

    private LinearLayout sickLeaveRow;

    private Employee employee;
    private Users user;
    private int employeeId;

    private YearlyEntitlementWS yearlyEntitlementWS;
    private LeaveTransactionWS leaveTransactionWS;
    private LeaveApplicationEmailNotificationWS leaveApplicationEmailWS;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((HomeActivity)getActivity()).enableNavigationDrawer(true);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_apply_leave, container, false);
        initLayout(rootView);

        ((HomeActivity) getActivity()).enableNavigationDrawer(true);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Apply Leave");
        setHasOptionsMenu(true);

        String username = ((HomeActivity) getActivity()).getUsername();
        String password = ((HomeActivity) getActivity()).getPassword();
        this.employee = ((HomeActivity) getActivity()).getActiveEmployee();
        this.user = (((HomeActivity) getActivity()).getActiveUser());
        this.employeeId = employee.getId();

        yearlyEntitlementWS = new YearlyEntitlementWS(username, password);
        leaveTransactionWS = new LeaveTransactionWS(username, password);
        leaveApplicationEmailWS = new LeaveApplicationEmailNotificationWS(username, password);

        PopulateYearlyEntitlementTask pop = new PopulateYearlyEntitlementTask();
        pop.execute();
        return rootView;
    }

    public void initLayout(final View rootView) {
        yearlyEntitlementList = new ArrayList<YearlyEntitlement>();

        editLeaveType = (Spinner) rootView.findViewById(R.id.editLeaveType);
        editLeaveType.setOnItemSelectedListener(this);

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
        thumbnail = (ImageView) rootView.findViewById(R.id.thumbnailAttachment);

        btnAttachment = (ImageButton) rootView.findViewById(R.id.btnAttachment);
        btnApplyLeave = (Button) rootView.findViewById(R.id.btnApplyLeave);

        sickLeaveRow = (LinearLayout) rootView.findViewById(R.id.rowSickLeave);

        editStartDate.setOnClickListener(this);
        editEndDate.setOnClickListener(this);
        btnAttachment.setOnClickListener(this);
        btnApplyLeave.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.editLeaveType:
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

                try {
                    setValues();
                    if (applyLeave()) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.info_applyleave, Toast.LENGTH_SHORT).show();
                        ((HomeActivity) getActivity()).switchFragment("home");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error processing leave application", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap;

        //Select picture from gallery
        if (requestCode == REQUEST_SELECT_PICTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.info_sickleaveattachment_galleryupload, Toast.LENGTH_SHORT).show();
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
            makeAvailableToGallery();
            Toast.makeText(getActivity().getApplicationContext(), R.string.info_sickleaveattachment_cameraupload, Toast.LENGTH_SHORT).show();
        } else {
            if (requestCode == REQUEST_IMAGE_CAPTURE)
                deleteFileImage();
            selectedImagePath = null;
        }

        if (selectedImagePath != null) {
            imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), THUMBSIZE, THUMBSIZE);
            retrieveFile(selectedImagePath, imageBitmap);
        }

    }

    //Gallery functions
    public void dispatchGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT); //File manager
        intent.setAction(Intent.ACTION_PICK); //Gallery
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE);
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
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_sickleaveattachment_filecreation, Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void makeAvailableToGallery() {
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

    public void deleteFileImage() {
        File file = new File(selectedImagePath);
        file.delete();
    }

    public void retrieveFile(String filepath, Bitmap imageBitmap) {
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
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byteData = baos.toByteArray();
    }

    public void updateUiValues(int position) {


        if (yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Sick leave")) {
            txtAttachment.setVisibility(View.VISIBLE);
            sickLeaveRow.setVisibility(View.VISIBLE);
        } else {
            txtAttachment.setVisibility(View.GONE);
            sickLeaveRow.setVisibility(View.GONE);
        }

        if (!(yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Unpaid leave") || yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Time-In-Lieu leave"))) {
            editEntitlement.setVisibility(View.VISIBLE);
            txtEntitlement.setVisibility(View.VISIBLE);
            editEntitlement.setText(String.valueOf(yearlyEntitlementList.get(position).getEntitlement()));

            editYearlyLeaveBal.setVisibility(View.VISIBLE);
            txtYearlyLeaveBal.setVisibility(View.VISIBLE);
            editYearlyLeaveBal.setText(String.valueOf(yearlyEntitlementList.get(position).getYearlyLeaveBalance()));

        } else {
            editEntitlement.setVisibility(View.GONE);
            txtEntitlement.setVisibility(View.GONE);

            editYearlyLeaveBal.setVisibility(View.GONE);
            txtYearlyLeaveBal.setVisibility(View.GONE);

        }

        if (yearlyEntitlementList.get(position).getLeaveType().getDescription().equals("Annual leave")) {
            editCurrentLeaveBal.setVisibility(View.VISIBLE);
            txtCurrentLeaveBal.setVisibility(View.VISIBLE);
            editCurrentLeaveBal.setText(String.valueOf(yearlyEntitlementList.get(position).getCurrentLeaveBalance()));
        } else {
            editCurrentLeaveBal.setVisibility(View.GONE);
            txtCurrentLeaveBal.setVisibility(View.GONE);
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


    private Double findAnnualYearlyEntitlement() {
        try {
            YearlyEntitlementFindAnnualTask findAnnualTask = new YearlyEntitlementFindAnnualTask();
            findAnnualTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return yearlyEntitlement.getYearlyLeaveBalance();
    }


    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
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
        if(StringUtils.isNotBlank(editNoOfDays.getText().toString()))
            setNumberOfDays(Double.valueOf(editNoOfDays.getText().toString()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date date;

        date = sdf.parse(editStartDate.getText().toString());
        setStartDate(date);

        date = sdf.parse(editEndDate.getText().toString());
        setEndDate(date);

        setReason(editReason.getText().toString());

    }

    public boolean applyLeave() /*throws LeaveApplicationException,RoleNotFound */ {
        // Validating whether number of days is half day or full day or not valid
        if (getNumberOfDays() != null) {
            double x = getNumberOfDays().doubleValue() - (long) getNumberOfDays().doubleValue();
            if (!(x == 0.0 || x == 0.5)) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_days_validation, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_days_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isEmployeeFinishedOneYear()) {
            //check if number of days applied is more than yearlyLeaveBalance
            if (!(Leave.UNPAID.equalsName(leaveType) || Leave.TIMEINLIEU.equalsName(leaveType))
                    && !(numberOfDays <= yearlyEntitlement.getYearlyLeaveBalance())) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_sick_validation, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            // validating applied leaves is in the range of current balance - applied leaves > = -3
            if ("Annual".equalsIgnoreCase(leaveType) && StringUtils.isNotBlank(leaveType) && StringUtils.isNotEmpty(leaveType)) {

                if (!(yearlyEntitlement.getCurrentLeaveBalance() - numberOfDays >= -3)) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_sick_validation, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (!("Unpaid".equalsIgnoreCase(leaveType) || Leave.TIMEINLIEU.equalsName(leaveType))
                    && StringUtils.isNotBlank(leaveType) && StringUtils.isNotEmpty(leaveType) && numberOfDays > yearlyEntitlement.getYearlyLeaveBalance()) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_sick_validation, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (numberOfDays < 0.5) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_applyleave_numberofdays, Toast.LENGTH_SHORT).show();

            return false;
        }
        // checking unpaid leaves allowing only maximum 30 days.
        if (Leave.UNPAID.equalsName(leaveType) && numberOfDays > 30) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_unpaid_validation, Toast.LENGTH_SHORT).show();
            return false;
        }
        //check if startdate after enddate
        if (startDate.after(endDate)) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.error_applyleave_datesrange, Toast.LENGTH_SHORT).show();
            return false;
        } else {

            leaveTransaction.setApplicationDate(new Date());
            leaveTransaction.setDeleted(false);
            leaveTransaction.setEmployee(getEmployee());
            leaveTransaction.setLeaveType(getYearlyEntitlement().getLeaveType());
            leaveTransaction.setNumberOfDays(getNumberOfDays());

            if (!Leave.TIMEINLIEU.equalsName(leaveTransaction.getLeaveType().getName()))
                leaveTransaction.setYearlyLeaveBalance(getYearlyEntitlement().getYearlyLeaveBalance());
            else
                leaveTransaction.setYearlyLeaveBalance(findAnnualYearlyEntitlement());
            if(StringUtils.isNotBlank(getReason()))
                leaveTransaction.setReason(getReason());
            else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.error_reason_empty, Toast.LENGTH_SHORT).show();
                return false;
            }
            leaveTransaction.setStartDateTime(getStartDate());
            leaveTransaction.setEndDateTime(getEndDate());


            if (getNumberOfDays().doubleValue() - (long) getNumberOfDays().doubleValue() == 0.5)
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
                    Toast.makeText(getActivity().getApplicationContext(), R.string.error_sickleaveattachment_mcNotFound, Toast.LENGTH_SHORT).show();
                    return false;
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

        return true;
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
            ArrayAdapter<YearlyEntitlement> spinnerAdapter = new ArrayAdapter<YearlyEntitlement>(getActivity().getApplicationContext(), R.layout.item_spinner, yearlyEntitlementList);
            spinnerAdapter.setDropDownViewResource(R.layout.item_spinner);
            editLeaveType.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<YearlyEntitlement> doInBackground(String... params) {
            return yearlyEntitlementWS.findYearlyEntitlementListByEmployee(employeeId);
        }
    }

    private class YearlyEntitlementFindOneTask extends AsyncTask<String, Void, hrm.com.model.YearlyEntitlement> {
        @Override
        protected void onPostExecute(YearlyEntitlement result) {
            super.onPostExecute(result);
            yearlyEntitlement = result;

            if (getYearlyEntitlement() != null) {
                allowedMaximumLeaves = 0.0;
                setLeaveType(getYearlyEntitlement().getLeaveType().getName());
                setYearlyBalance(getYearlyEntitlement().getYearlyLeaveBalance());
                if (isEmployeeFinishedOneYear()) {
                    if (Leave.UNPAID.equalsName(leaveType))
                        allowedMaximumLeaves = 30.0;
                    else
                        allowedMaximumLeaves = getYearlyEntitlement().getYearlyLeaveBalance();
                } else {
                    if (Leave.ANNUAL.equalsName(leaveType))
                        allowedMaximumLeaves = getYearlyEntitlement().getCurrentLeaveBalance() + 3.0;
                    else if (Leave.UNPAID.equalsName(leaveType))
                        allowedMaximumLeaves = 30.0;
                    else
                        allowedMaximumLeaves = getYearlyEntitlement().getYearlyLeaveBalance();
                }
            }

        }

        @Override
        protected YearlyEntitlement doInBackground(String... params) {
            return yearlyEntitlementWS.findOne(selectedYearlyEntitlement);
        }
    }

    private class YearlyEntitlementFindAnnualTask extends AsyncTask<String, Void, hrm.com.model.YearlyEntitlement> {
        @Override
        protected void onPostExecute(YearlyEntitlement result) {
            super.onPostExecute(result);
            yearlyEntitlement = result;
        }

        @Override
        protected YearlyEntitlement doInBackground(String... params) {
            return yearlyEntitlementWS.findAnnualYearlyEntitlementOfEmployee(employeeId);
        }
    }

    private class ProcessAppliedLeaveOfEmployeeTask extends AsyncTask<String, Void, LeaveTransaction> {

        @Override
        protected void onPostExecute(LeaveTransaction result) {
            super.onPostExecute(result);
            leavePersistBean = result;

            SendIntimationEmailTask sendEmail = new SendIntimationEmailTask();
            sendEmail.execute();

        }

        @Override
        protected LeaveTransaction doInBackground(String... params) {
            return leaveTransactionWS.processAppliedLeaveOfEmployee(leaveTransaction);
        }

    }

    private class GetLeaveRuleByRoleAndLeaveTypeTask extends AsyncTask<String, Void, LeaveRuleBean> {

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
            return leaveTransactionWS.getLeaveRuleByRoleAndLeaveType(leaveRuleWrapper);

        }

    }

    private class SendIntimationEmailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
            setSelectedYearlyEntitlement(0);
            setLeaveType("");
            setStartDate(null);
            setEndDate(null);
            setReason("");

        }

        @Override
        protected Void doInBackground(String... params) {
            return leaveApplicationEmailWS.sendingIntimationEmail(leavePersistBean);
        }
    }

    private class SaveLeaveApprovalDecisionsTask extends AsyncTask<String, Void, LeaveFlowDecisionsTaken> {
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
            return leaveTransactionWS.saveLeaveApprovalDecisions();
        }
    }
}
