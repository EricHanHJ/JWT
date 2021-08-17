
package com.planty.jwtresearch.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // Database Table과 1:1로 매핑되는 객체
@Table(name = "user") // 객체와 매핑되는 Database의 Table명을 지정
@Getter // Lombok Getter
@Setter // Lombok Setter
@Builder // Lombok Builder
@AllArgsConstructor // Lombok Constructor
@NoArgsConstructor // Lombok Constructor
public class User {

	@JsonIgnore // 서버에서 Json 응답을 생성할때 해당 필드는 ignore
	@Id	// 해당키는 Primary Key
	@Column(name = "user_id")	// Database Column의 정보
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "username", length = 50, unique = true)
	private String username;

	@JsonIgnore // 서버에서 Json 응답을 생성할때 해당 필드는 ignore
	@Column(name = "password", length = 100)
	private String password;

	@Column(name = "nickname", length = 50)
	private String nickname;

	@JsonIgnore
	@Column(name = "activated")
	private boolean activated;

	@ManyToMany	// 다대다(User테이블과 Authority테이블의 관계 Join 정의
	@JoinTable(name = "user_authority", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "user_id") }, inverseJoinColumns = {
					@JoinColumn(name = "authority_name", referencedColumnName = "authority_name") })
	private Set<Authority> authorities;
}
