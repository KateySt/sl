package starlight.backend.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import starlight.backend.exception.AuthorizationFailureException;
import starlight.backend.exception.EmailAlreadyOccupiedException;
import starlight.backend.exception.PageNotFoundException;
import starlight.backend.exception.YouAreInDeletingProcess;
import starlight.backend.exception.kudos.*;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.exception.proof.UserAccesDeniedToProofException;
import starlight.backend.exception.proof.UserCanNotEditProofNotInDraftException;
import starlight.backend.exception.user.UserNotFoundInDelayedDeleteRepository;
import starlight.backend.exception.user.UserNotFoundWithUUIDException;
import starlight.backend.exception.user.sponsor.SponsorAlreadyOnDeleteList;
import starlight.backend.exception.user.sponsor.SponsorCanNotSeeAnotherSponsor;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler({
            PageNotFoundException.class,
            UserCanNotEditProofNotInDraftException.class,
            UserAccesDeniedToProofException.class,
            UserCannotAddKudosToTheirAccount.class,
            KudosRequestMustBeNotZeroException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO badRequest(Exception exception) {
        return new ErrorDTO(exception.getMessage());
    }

    @ExceptionHandler({
            EmailAlreadyOccupiedException.class,
            ProofAlreadyHaveKudosFromUser.class,
            SponsorAlreadyOnDeleteList.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO alreadyIs(Exception exception) {
        return new ErrorDTO(exception.getMessage());
    }

    @ExceptionHandler({
            AuthorizationFailureException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO authFailure(Exception exception) {
        return new ErrorDTO(exception.getMessage());
    }

    @ExceptionHandler({
            ProofNotFoundException.class,
            TalentNotFoundException.class,
            SponsorNotFoundException.class,
            UserNotFoundInDelayedDeleteRepository.class,
            UserNotFoundWithUUIDException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO notExists(Exception exception) {
        return new ErrorDTO(exception.getMessage());
    }

    @ExceptionHandler({
            TalentCanNotAddKudos.class,
            NotEnoughKudosException.class,
            SponsorCanNotSeeAnotherSponsor.class,
            YouCanNotReturnMoreKudosThanGaveException.class,
            YouAreInDeletingProcess.class,
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO forbidden(Exception exception) {
        return new ErrorDTO(exception.getMessage());
    }
    // @ExceptionHandler({
    //         Exception.class
    // })
    // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    // public ErrorDTO exception(Exception e) {
    //     if (e instanceof SQLException) {
    //         if (!ignoreSQLException(
    //                 ((SQLException) e).
    //                         getSQLState())) {

    //             e.printStackTrace(System.err);
    //             System.err.println("SQLState: " +
    //                     ((SQLException) e).getSQLState());

    //             System.err.println("Error Code: " +
    //                     ((SQLException) e).getErrorCode());

    //             System.err.println("Message: " + e.getMessage());

    //             Throwable t = e.getCause();
    //             while (t != null) {
    //                 System.out.println("Cause: " + t);
    //                 t = t.getCause();
    //             }
    //         }
    //     }
    //     return new ErrorDTO(e.getMessage());
    // }

    // public static boolean ignoreSQLException(String sqlState) {

    //     if (sqlState == null) {
    //         System.out.println("The SQL state is not defined!");
    //         return false;
    //     }

    //     // X0Y32: Jar file already exists in schema
    //     if (sqlState.equalsIgnoreCase("X0Y32"))
    //         return true;

    //     // 42Y55: Table already exists in schema
    //     if (sqlState.equalsIgnoreCase("42Y55"))
    //         return true;

    //     return false;
    // }

    record ErrorDTO(String message) {
    }
}