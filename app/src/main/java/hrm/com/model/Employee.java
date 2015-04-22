package hrm.com.model;


import java.io.Serializable;
import java.util.Date;
/**
 * Created by Beans on 3/29/2015.
 */
public class Employee implements Serializable {

    private int id;
    private String employeeNumber;
    private String name;
    private String position;
    private String idNumber;
    private String passportNumber;
    private String gender;
    private String religion;
    private String maritalStatus;
    private String workEmailAddress;
    private String personalEmailAddress;
    private String officePhone;
    private String personalPhone;
    private String nationality;
    private Users users;
    private EmployeeGrade employeeGrade;
    private Department department;
    private EmployeeType employeeType;
    private Date joinDate;
    private Date resignationDate;
    private boolean isDeleted;
    private boolean isResigned;
    private String createdBy;
    private Date creationTime;
    private String lastModifiedBy;
    private Date lastModifiedTime;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public String getIdNumber() {
        return idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getReligion() {
        return religion;
    }
    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getWorkEmailAddress() {
        return workEmailAddress;
    }
    public void setWorkEmailAddress(String workEmailAddress) {
        this.workEmailAddress = workEmailAddress;
    }

    public String getPersonalEmailAddress() {
        return personalEmailAddress;
    }
    public void setPersonalEmailAddress(String personalEmailAddress) {
        this.personalEmailAddress = personalEmailAddress;
    }

    public String getOfficePhone() {
        return officePhone;
    }
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }
    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Users getUsers() {
        return users;
    }
    public void setUsers(Users users) {
        this.users = users;
    }

    public EmployeeGrade getEmployeeGrade() {
        return employeeGrade;
    }
    public void setEmployeeGrade(EmployeeGrade employeeGrade) {
        this.employeeGrade = employeeGrade;
    }

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }
    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public Date getJoinDate() {
        return joinDate;
    }
    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getResignationDate() {
        return resignationDate;
    }
    public void setResignationDate(Date resignationDate) {
        this.resignationDate = resignationDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isResigned() {
        return isResigned;
    }
    public void setResigned(boolean isResigned) {
        this.isResigned = isResigned;
    }
    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }
    @Override
    public String toString() {
        return "Employee [id=" + id + ", employeeNumber=" + employeeNumber
                + ", name=" + name + ", position=" + position + ", idNumber="
                + idNumber + ", passportNumber=" + passportNumber + ", gender="
                + gender + ", religion=" + religion + ", maritalStatus="
                + maritalStatus + ", workEmailAddress=" + workEmailAddress
                + ", personalEmailAddress=" + personalEmailAddress
                + ", officePhone=" + officePhone + ", personalPhone="
                + personalPhone + ", nationality=" + nationality + ", users="
                + users + ", employeeGrade=" + employeeGrade + ", department="
                + department + ", employeeType=" + employeeType + ", joinDate="
                + joinDate + ", resignationDate=" + resignationDate
                + ", isDeleted=" + isDeleted + ", isResigned=" + isResigned
                + ", createdBy=" + createdBy + ", creationTime=" + creationTime
                + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedTime="
                + lastModifiedTime + "]";
    }


}
