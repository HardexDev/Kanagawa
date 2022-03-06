package kanagawa.models;

import java.util.ArrayList;
import kanagawa.models.enums.Bonus;
import kanagawa.models.enums.Skill;
import kanagawa.models.enums.UVCategory;

/**
 * Class representing a player of the game.
 */
public class Player {

    private String username;

    /**
     * Indicates if this player is the one to begin the round.
     */
    private boolean isFirstPlayer;

    /**
     * Indicated if the player is currently playing his turn.
     */
    private boolean isPlaying;

    private Game game;

    private Inventory inventory;

    /**
     * Constructor of the {@code Player} class.
     * 
     * @param username name of the player.
     */
    public Player(String username) {
        this.username = username;
        this.isFirstPlayer = false;
        this.isPlaying = false;
        this.inventory = new Inventory();

        game = Game.getGameInstance();
    }

    // Getters et setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Indicates if this player is the one to begin the round.
     */
    public boolean isFirstPlayer() {
        return isFirstPlayer;
    }

    /**
     * Sets if this player is the one to begin the round.
     */
    public void setFirstPlayer(boolean firstPlayer) {
        isFirstPlayer = firstPlayer;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Indicated if the player is currently playing his turn.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Sets if the player is currently playing his turn.
     */
    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    /**
     * Adds the {@code PersonalWork} part of a {@code Card} to the inventory of the
     * player.
     * 
     * @param card
     */
    public void addToPersonalWork(Card card) {
        inventory.addPersonalWork(card.getPersonalWork());

        if (card.getPersonalWork().getBonus() == Bonus.PEN) {
            inventory.setPenCount(inventory.getPenCount() + 1);
        }
        if (card.getPersonalWork().getBonus() == Bonus.CREDIT) {
            this.inventory.setCredits(inventory.getCredits() + 1);
        }
        if (card.getPersonalWork().getBonus() == Bonus.DOUBLE_CREDIT) {
            this.inventory.setCredits(inventory.getCredits() + 2);
        }
        if (card.getPersonalWork().getBonus() == Bonus.PROFESSOR) {
            for (Player player : game.getPlayers())
                player.getInventory().setHasProfessor(false);
            this.inventory.setHasProfessor(true);
        }
    }

    /**
     * Adds the {@code UV} part of a {@code Card} to the inventory of the
     * player.
     * 
     * @param card
     */
    public void addToUv(Card card) {
        inventory.addUv(card.getUv());
    }

    /**
     * Checks if the player possesses the required {@code Skill} and if a pen is on
     * it.
     * 
     * @param skill {@code Skill} to test
     * @return a {@code boolean}
     */
    public boolean hasSkillAvailable(Skill skill) {
        int i = 0;
        boolean isSkillAvailable = false;
        PersonalWork pwToTest;
        boolean foundSkill = false;

        while (!foundSkill) {
            if (i < inventory.getPwPossessed().size()) {
                pwToTest = inventory.getPwPossessed().get(i);
                if (pwToTest.getSkill() == skill) {
                    if (pwToTest.hasPen()) {
                        isSkillAvailable = true;
                        foundSkill = true;
                    }
                }
                i++;
            } else {
                foundSkill = true;
            }
        }
        return isSkillAvailable;
    }

    /**
     * Adds 1 pen to the inventory of the player.
     */
    public void addPen() {
        inventory.setPenCount(inventory.getPenCount() + 1);
    }

    public void removePen() {
        inventory.setPenCount(inventory.getPenCount() - 1);
    }

    /**
     * Number of pens the user has.
     */
    public int getPenCount() {
        return inventory.getPenCount();
    }

    /**
     * Checks if the number of pen possessed by the player is greater than 0.
     * 
     * @return a {@code boolean}
     */
    public boolean checkPenCount() {
        return getPenCount() > 0;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", isFirstPlayer=" + isFirstPlayer +
                ", game=" + game +
                ", inventory=" + inventory +
                '}';
    }

    /**
     * Finds all diplomas available to the user according to the content of its
     * {@code Inventory}.
     * 
     * @return an {@code ArrayList<Diploma>} of the available diplomas or
     *         {@code null} if no diplomas are available.
     */
    public ArrayList<Diploma> findAvailableDiplomas() {
        ArrayList<Diploma> availableDiplomas = new ArrayList<Diploma>();

        ArrayList<DiplomaGroup> diplomaGroups = game.getDiplomaGroups();

        ArrayList<Diploma> refusedDiplomas = inventory.getRefusedDiplomas();
        ArrayList<DiplomaGroup> unavailableDiplomaGroups = inventory.getUnavailableDiplomaGroups();

        int[] totalUVsPossessed = new int[UVCategory.length];
        int[] totalSkillsPossessed = new int[Skill.length];

        // We compute the total of UVs the user possesses in each ECTS category
        for (UV currentUV : inventory.getUvPossessed()) {
            totalUVsPossessed[currentUV.getUvCategory().toInt()] += 1;
        }

        // We compute the total of skills the user possesses in each category
        for (PersonalWork currentPW : inventory.getPwPossessed()) {
            totalSkillsPossessed[currentPW.getSkill().toInt()] += 1;
        }

        for (DiplomaGroup diplomaGroup : diplomaGroups) {
            // We check if the user does not possess a diploma of this group yet
            if (!unavailableDiplomaGroups.contains(diplomaGroup)) {

                for (Diploma diploma : diplomaGroup.getDiplomas()) {
                    // We check if the diploma is available and if the user has not refused or taken
                    // this diploma yet

                    if (!refusedDiplomas.contains(diploma) && !inventory.getDiplomaPossessed().contains(diploma)) {
                        int[] necessaryUVs = diploma.getUVArray();
                        int[] necessarySkills = diploma.getSkillArray();

                        // We check if he has enough UVs in each category
                        boolean hasRequiredUVs = true;
                        for (int i = 0; i < necessaryUVs.length; i++) {
                            if (totalUVsPossessed[i] < necessaryUVs[i]) {
                                hasRequiredUVs = false;
                                break;
                            }
                        }

                        // We check if he has enough skills in each category
                        boolean hasRequiredSkills = true;
                        for (int i = 0; i < necessarySkills.length; i++) {
                            if (totalSkillsPossessed[i] < necessarySkills[i]) {
                                hasRequiredSkills = false;
                                break;
                            }
                        }

                        // If all conditions are fulfilled, we add the diploma to the list of available
                        // diplomas
                        if (hasRequiredUVs && hasRequiredSkills)
                            availableDiplomas.add(diploma);
                    }
                }
            }
        }

        return availableDiplomas.isEmpty() ? null : availableDiplomas;
    }
}
