package hrm.com.model;

/**
 * Created by Beans on 3/29/2015.
 */

import java.io.Serializable;
import java.util.Date;

public class AccessRights implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String accessRights;
    private String description;
    private String createdBy;
    private Date creationTime;
    private String lastModifiedBy;
    private Date lastModifiedTime;
    private boolean isDeleted = false;


    public AccessRights(int id, String accessRights, String description,
                        String createdBy, Date creationTime, String lastModifiedBy,
                        Date lastModifiedTime, boolean isDeleted) {
        super();
        this.id = id;
        this.accessRights = accessRights;
        this.description = description;
        this.createdBy = createdBy;
        this.creationTime = creationTime;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.isDeleted = isDeleted;
    }

    public AccessRights(){

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(String accessRights) {
        this.accessRights = accessRights;
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

    public boolean equals(Object obj) {
        AccessRights inputAccessRights = (AccessRights) obj;
        if(getId() == inputAccessRights.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return id;
    }

}
