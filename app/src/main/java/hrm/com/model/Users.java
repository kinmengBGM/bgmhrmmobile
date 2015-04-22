package hrm.com.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Beans on 3/29/2015.
 */
public class Users implements Serializable {

    private int id;
    private String username;
    private String password;
    private boolean enabled;
    private String createdBy;
    private java.util.Date creationTime;
    private String lastModifiedBy;
    private java.util.Date lastModifiedTime;
    private Set<Role> userRoles = new HashSet<Role>();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

}
