package de.superchat.message.substitution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SubstitutionServiceImpl implements SubstitutionService {

    @Inject
    UserSubstitution userSubstitution;
    @Inject
    BTCSubstitution btcSubstitution;

    @Override
    public String substitute(String message) {
        Substitution[] substitutions = {userSubstitution, btcSubstitution};

        for (Substitution substitution : substitutions) {
            int lastIndex = 0;
            StringBuilder output = new StringBuilder();
            Pattern placeholderPattern = Pattern.compile(substitution.getPlaceholderPattern());
            Matcher matcher = placeholderPattern.matcher(message);
            while (matcher.find()) {
                output.append(message, lastIndex, matcher.start())
                    .append(substitution.substitute(matcher.group(0)));

                lastIndex = matcher.end();
            }
            if (lastIndex < message.length()) {
                output.append(message, lastIndex, message.length());
            }
            message = output.toString();
        }

        return message;
    }
}
