package hrm.com.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Beans on 3/29/2015.
 */
public class Role implements Serializable{
    private int id;
    private String role;
    private boolean isDeleted= false;
    private String description;
    private String createdBy;
    private java.util.Date creationTime;
    private String lastModifiedBy;
    private java.util.Date lastModifiedTime;
    private Set<AccessRights> accessRights = new HashSet<AccessRights>();

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isDeleted() {
        return isDeleted;
    }


    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
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

    public Set<AccessRights> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(Set<AccessRights> accessRights) {
        this.accessRights = accessRights;
    }

    public boolean equals(Object obj) {
        Role inputRole = (Role) obj;
        if(getId() == inputRole.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return id;
    }
}
