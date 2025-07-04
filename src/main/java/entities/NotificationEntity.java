package entities;

import jakarta.persistence.*;

@Table(name = "notifications", schema="va_assessment")
@Entity
public class NotificationEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int")
    protected Long id;
	
    @Column(name = "phone_number", columnDefinition = "VARCHAR(20)", nullable = false)
    protected String phoneNumber;
    
    @Column(name = "personalization", columnDefinition = "VARCHAR(25)", nullable = true)
    protected String personalization;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
    protected TemplateEntity templateEntity;
    
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPersonalization() {
		return personalization;
	}

	public void setPersonalization(String personalization) {
		this.personalization = personalization;
	}

	public TemplateEntity getTemplateEntity() {
		return templateEntity;
	}

	public void setTemplateEntity(TemplateEntity templateEntity) {
		this.templateEntity = templateEntity;
	}

    
}
