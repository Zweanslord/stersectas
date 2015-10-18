package stersectas.domain.game;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import stersectas.documentation.HibernateConstructor;
import stersectas.domain.user.UserId;

@MappedSuperclass
public abstract class Game {

	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	private Name name;

	@Embedded
	private Description description;

	@Embedded
	private MaximumPlayers maximumPlayers;

	@Embedded
	@AttributeOverride(name = "id", column = @Column(name = "masterId", nullable = false, unique = true))
	private UserId masterId;

	@HibernateConstructor
	protected Game() {
	}

	protected Game(
			Name name,
			Description description,
			MaximumPlayers maximumPlayers,
			UserId masterId) {
		this.name = name;
		this.description = description;
		this.maximumPlayers = maximumPlayers;
		this.masterId = masterId;
	}

	protected void changeName(Name name) {
		this.name = name;
	}

	protected void changeDescription(Description description) {
		this.description = description;
	}

	protected void adjustMaximumOfPlayers(MaximumPlayers maximumPlayers) {
		this.maximumPlayers = maximumPlayers;
	}

	public Name name() {
		return name;
	}

	public Description description() {
		return description;
	}

	public MaximumPlayers maximumPlayers() {
		return maximumPlayers;
	}

	public UserId masterId() {
		return masterId;
	}

}