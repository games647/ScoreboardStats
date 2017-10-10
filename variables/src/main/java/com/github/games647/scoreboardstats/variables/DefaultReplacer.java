package com.github.games647.scoreboardstats.variables;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents that a replacer that is added to this plugin by this plugin itself.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultReplacer {

    /**
     * Returns the required plugin that is needed to activate this variables. It defaults to
     * this scoreboard plugin itself.
     *
     * @return the required plugin dependency for this variables
     */
    String plugin() default "ScoreboardStats";

    /**
     * Minimum required version to activate the replacers that will be registered. The version format
     * have to be in semVer format major.minor.fix with
     * <br>
     * 1.2.3 is higher than 1.1.3
     * <br>
     * 2.5 higher than 1.0.0
     *
     * @return required version or empty for no required version
     */
    String requiredVersion() default "";
}
