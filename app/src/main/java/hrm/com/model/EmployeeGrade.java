package hrm.com.model;

import java.io.Serializable;

/**
 * Created by Beans on 3/29/2015.
 */
public class EmployeeGrade implements Serializable {
    private int id;
    private String name;
    private String description;
    private boolean isDeleted = false;
    private java.util.Date creationTime;
    private String createdBy;
    private java.util.Date lastModifiedTime;
    private String lastModifiedBy;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setCreationTime(java.util.Date creationTime) {
        this.creationTime = creationTime;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setLastModifiedTime(java.util.Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public java.util.Date getCreationTime() {
        return creationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public java.util.Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public boolean equals(Object other)
    {
        return other instanceof EmployeeGrade && id == ((EmployeeGrade) other).getId();
    }

    public int hashCode()
    {
        return this.getClass().hashCode();
    }

    public String toString()
    {
        return "EmployeeGrade[" + getId() + "," + getName() + "]";
    }


}
