package global.services.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gwt.view.client.ProvidesKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Application implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
	@Persistent
	private String userId_;
	@Persistent
	private String appName_;
	@Persistent
	private String appTittle_;
	private int scoreEntries_;
	private int noteEntries_;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<Application> KEY_PROVIDER = new ProvidesKey<Application>() {
      public Object getKey(Application app) {
        return app == null ? null : app.getId();
      }
    };
    
    
	public Application() {}
	public Application(String userID) {
		userId_ = userID;
	}
	
	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public String getAppName() {
		return appName_;
	}

	public void setAppName(String appId) {
		appName_ = appId;
	}

	public String getAppTittle() {
		return appTittle_;
	}

	public void setAppTittle(String appTittle) {
		appTittle_ = appTittle;
	}
	public void setScoreEntries(int scoreEntries) {
		this.scoreEntries_ = scoreEntries;
	}
	public int getScoreEntries() {
		return scoreEntries_;
	}
	public void setUserId(String userId) {
		this.userId_ = userId;
	}
	public String getUserId() {
		return userId_;
	}
	public void setNoteEntries(int noteEntries) {
		this.noteEntries_ = noteEntries;
	}
	public int getNoteEntries() {
		return noteEntries_;
	}

}
