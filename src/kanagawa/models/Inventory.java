package kanagawa.models;

import java.util.ArrayList;

import kanagawa.models.enums.Bonus;
import kanagawa.models.enums.Skill;

/**
 * Class implementing the inventory of the {@code Player}. The inventory
 * contains all the items possessed by the player.
 */
public class Inventory {

    private int credits;

    /**
     * Number of pens the user has.
     */
    private int penCount;

    private ArrayList<PersonalWork> pwPossessed;

    private ArrayList<UV> uvPossessed;

    private ArrayList<Diploma> diplomaPossessed;

    /**
     * List of the diplomas that the player has already refused.
     */
    private ArrayList<Diploma> refusedDiplomas;

    /**
     * List of the groups in which the player already has got a diploma.
     */
    private ArrayList<DiplomaGroup> unavailableDiplomaGroups;

    /**
     * If {@code true}, this player will be the one to begin the next round.
     */
    private boolean hasProfessor;

    /**
     * Constructor of {@code Card} class.
     */
    public Inventory() {
        this.credits = 0;
        this.penCount = 2;
        this.pwPossessed = new ArrayList<PersonalWork>();
        this.uvPossessed = new ArrayList<UV>();
        this.diplomaPossessed = new ArrayList<Diploma>();
        this.refusedDiplomas = new ArrayList<Diploma>();
        this.unavailableDiplomaGroups = new ArrayList<DiplomaGroup>();
        this.hasProfessor = false;
    }

    public int getCredits() {
        return credits;
    }

    public ArrayList<PersonalWork> getPwPossessed() {
        return pwPossessed;
    }

    public ArrayList<UV> getUvPossessed() {
        return uvPossessed;
    }

    public ArrayList<Diploma> getDiplomaPossessed() {
        return diplomaPossessed;
    }

    /**
     * Returns the list of the diplomas that the player has already refused.
     */
    public ArrayList<Diploma> getRefusedDiplomas() {
        return refusedDiplomas;
    }

    /**
     * Returns the list of the groups in which the player already has got a diploma.
     */
    public ArrayList<DiplomaGroup> getUnavailableDiplomaGroups() {
        return unavailableDiplomaGroups;
    }

    /**
     * If {@code true}, this player will be the one to begin the next round.
     */
    public boolean hasProfessor() {
        return hasProfessor;
    }

    /**
     * Number of pens the user has.
     */
    public int getPenCount() {
        return penCount;
    }

    // Setters
    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setPenCount(int penCount) {
        this.penCount = penCount;
    }

    /**
     * Sets if the player will be the one to begin the next round.
     * 
     * @param hasProfessor If {@code true}, this player will be the one to begin the
     *                     next round
     */
    public void setHasProfessor(boolean hasProfessor) {
        this.hasProfessor = hasProfessor;
    }

    /**
     * Adds a diploma to the inventory of the player, adds the group of the added
     * diploma to the list of unavailable diploma groups and removes the diploma
     * from its {@code DiplomaGroup} so that it is no longer available.
     * 
     * @param diploma
     */
    public void addDiploma(Diploma diploma) {
        if (this.diplomaPossessed.contains(diploma)) {
            System.err.println("Inventory.addDiploma() : Diploma already possessed.");
            return;
        }
        this.diplomaPossessed.add(diploma);
        this.unavailableDiplomaGroups.add(diploma.getGroup());
        diploma.getGroup().getDiplomas().remove(diploma);
        credits += diploma.getCredit();
    }

    /**
     * Adds a refused diploma to the inventory of the player.
     * 
     * @param diploma
     */
    public void addRefusedDiploma(Diploma diploma) {
        if (this.refusedDiplomas.contains(diploma)) {
            System.err.println("Inventory.addRefusedDiploma() : Diploma already refused.");
        }
        this.refusedDiplomas.add(diploma);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "credits=" + credits +
                ", pwPossessed=" + pwPossessed +
                ", uvPossessed=" + uvPossessed +
                ", diplomaPossessed=" + diplomaPossessed +
                ", hasProfessor=" + hasProfessor +
                '}';
    }

    public void addPersonalWork(PersonalWork pw) {
        pwPossessed.add(pw);
        if (pw.getBonus() == Bonus.PROFESSOR) {
            setHasProfessor(true);
        }
    }

    public void addUv(UV uv) {
        uvPossessed.add(uv);
    }

    /**
     * Computes the number of points the player possesses in a given {@code Skill}.
     * 
     * @param skill The {@code Skill} category to analyse
     * @return an {@code int}
     */
    public int getSkillCount(Skill skill) {
        int count = 0;
        for (PersonalWork pw : pwPossessed) {
            if (pw.getSkill() == skill) {
                count++;
            }
        }
        return count;
    }
}
