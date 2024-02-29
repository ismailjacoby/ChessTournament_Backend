package be.technobel.chesstournament.bll.mailing;

import be.technobel.chesstournament.dal.models.entities.TournamentEntity;
import be.technobel.chesstournament.dal.models.entities.UserEntity;
import be.technobel.chesstournament.dal.models.enums.Gender;
import be.technobel.chesstournament.dal.repositories.UserRepository;
import be.technobel.chesstournament.pl.models.forms.TournamentForm;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Service class for sending various types of emails in the Chess Tournament application.
 */
@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private final UserRepository userRepository;


    public EmailSenderService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Sends a registration email to a user with the provided username and password.
     *
     * @param email    the recipient's email address.
     * @param username the username for the new user.
     * @param password the password for the new user.
     */
    public void sendRegistrationEmail(String email, String username, String password) {
        String subject = "Welcome to Mr. Checkmate's Chess Club!";
        String body = processRegistrationEmailTemplate(username,password);

        sendEmail(email, subject, body);
    }

    /**
     * Sends a tournament creation email to eligible players based on specified criteria.
     *
     * @param name              the name of the tournament.
     * @param location          the location of the tournament.
     * @param minElo            the minimum Elo rating for players to be eligible.
     * @param maxElo            the maximum Elo rating for players to be eligible.
     * @param minPlayers        the minimum number of players required for the tournament.
     * @param maxPlayers        the maximum number of players allowed for the tournament.
     * @param womenOnly         whether the tournament is open only to female players.
     * @param registrationEndDate the date when tournament registrations end.
     */
    public void sendTournamentCreationEmail(String name, String location, int minElo, int maxElo, int minPlayers, int maxPlayers, boolean womenOnly, Date registrationEndDate){
        String subject = "New Tournament Created";
        String body = processTournamentCreationEmailTemplate(name,location,minElo,maxElo,minPlayers,maxPlayers,womenOnly,registrationEndDate);
        List<UserEntity> playerEmails = userRepository.getAllPlayers();
        List<String> filteredPlayerEmails = filterPlayers(playerEmails, minElo, maxElo,womenOnly);


        for (String email : filteredPlayerEmails) {
            sendEmail(email, subject, body);
        }
    }

    /**
     * Sends a tournament cancellation email to all participants.
     *
     * @param name        the name of the canceled tournament.
     * @param location    the location of the canceled tournament.
     * @param participants the set of participants in the canceled tournament.
     */
    public void sendTournamentWithParticipantsCancelled(String name, String location, Set<UserEntity> participants){
        String subject = name + " Tournament just got cancelled";
        String body = processTournamentCancellationEmailTemplate(name,location);

        for (UserEntity player : participants) {
            sendEmail(player.getEmail(), subject, body);
        }
    }

    /**
     * Sends a tournament cancellation email to all players.
     *
     * @param name     the name of the canceled tournament.
     * @param location the location of the canceled tournament.
     */
    public void sendTournamentCancelled(String name, String location){
        String subject = name + " Tournament just got cancelled";
        String body = processTournamentCancellationEmailTemplate(name,location);

        for (UserEntity player : userRepository.getAllPlayers()) {
            sendEmail(player.getEmail(), subject, body);
        }
    }

    /**
     * Processes the Thymeleaf template for the registration email.
     *
     * @param username the username for the new user.
     * @param password the password for the new user.
     * @return the processed email body.
     */
    private String processRegistrationEmailTemplate(String username, String password){
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("password", password);
        return templateEngine.process("email/registrationEmail",context);
    }

    /**
     * Processes the Thymeleaf template for the tournament creation email.
     *
     * @param name              the name of the tournament.
     * @param location          the location of the tournament.
     * @param minElo            the minimum Elo rating for players to be eligible.
     * @param maxElo            the maximum Elo rating for players to be eligible.
     * @param minPlayers        the minimum number of players required for the tournament.
     * @param maxPlayers        the maximum number of players allowed for the tournament.
     * @param womenOnly         whether the tournament is open only to female players.
     * @param registrationEndDate the date when tournament registrations end.
     * @return the processed email body.
     */
    private String processTournamentCreationEmailTemplate(String name, String location, int minElo, int maxElo, int minPlayers, int maxPlayers, boolean womenOnly, Date registrationEndDate) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("location", location);
        context.setVariable("minElo", minElo);
        context.setVariable("maxElo", maxElo);
        context.setVariable("minPlayers", minPlayers);
        context.setVariable("maxPlayers", maxPlayers);

        if(womenOnly){
            context.setVariable("gender", "Women Only");
        } else if (!womenOnly) {
            context.setVariable("gender", "All Genders");
        }

        context.setVariable("registrationEndDate", registrationEndDate);

        return templateEngine.process("email/tournamentCreationEmail", context);
    }

    /**
     * Processes the Thymeleaf template for the tournament cancellation email.
     *
     * @param name     the name of the canceled tournament.
     * @param location the location of the canceled tournament.
     * @return the processed email body.
     */
    private String processTournamentCancellationEmailTemplate(String name, String location){
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("location", location);

        return templateEngine.process("email/tournamentCancelledEmail",context);
    }

    /**
     * Sends an email with the specified subject and body to the given email address.
     *
     * @param email   the recipient's email address.
     * @param subject the subject of the email.
     * @param body    the body of the email.
     */
    public void sendEmail(String email, String subject, String body){
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true,"UTF-8");

            helper.setFrom("checkmatemonsieur@gmail.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

            System.out.println("Mail sent successfully!");
        } catch (MessagingException me){
            me.printStackTrace();
        }

    }

    /**
     * Filters eligible players based on specified criteria (Elo rating, gender).
     *
     * @param players   the list of all players.
     * @param minElo    the minimum Elo rating for players to be eligible.
     * @param maxElo    the maximum Elo rating for players to be eligible.
     * @param womenOnly whether the tournament is open only to female players.
     * @return a list of email addresses of eligible players.
     */
    private List<String> filterPlayers(List<UserEntity> players,int minElo, int maxElo,boolean womenOnly) {
        List<String> filteredPlayerEmails = new ArrayList<>();

        for (UserEntity player : players) {
            boolean isEligible = player.getElo() >= minElo && player.getElo() <= maxElo;

            if(isEligible){
                // If the tournament is female only send to Females else send to everyone
                if (womenOnly && player.getGender() == Gender.FEMALE){
                    filteredPlayerEmails.add(player.getEmail());
                } else if (womenOnly && player.getGender() == Gender.FEMALE && isJunior(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                } else if (womenOnly && player.getGender() == Gender.FEMALE && isSenior(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                } else if (womenOnly && player.getGender() == Gender.FEMALE && isVeteran(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                } else if (isJunior(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                } else if (isSenior(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                } else if (isVeteran(player.getDateOfBirth())) {
                    filteredPlayerEmails.add(player.getEmail());
                }
            }
        }

        return filteredPlayerEmails;
    }

    /**
     * Checks if a player is a junior based on their date of birth.
     *
     * @param dateOfBirth the player's date of birth.
     * @return true if the player is a junior; otherwise, false.
     */
    private boolean isJunior(Date dateOfBirth) {
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();

        return age < 18;
    }

    /**
     * Checks if a player is a Senior based on their date of birth.
     *
     * @param dateOfBirth the player's date of birth.
     * @return true if the player is a Senior; otherwise, false.
     */
    private boolean isSenior(Date dateOfBirth) {
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();

        return age >= 18 && age < 60;
    }

    /**
     * Checks if a player is a Veteran based on their date of birth.
     *
     * @param dateOfBirth the player's date of birth.
     * @return true if the player is a Veteran; otherwise, false.
     */
    private boolean isVeteran(Date dateOfBirth) {
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        Period period = Period.between(birthDate, currentDate);
        int age = period.getYears();

        return age >= 60;
    }
}
