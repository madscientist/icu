// © 2017 and later: Unicode, Inc. and others.
// License & terms of use: http://www.unicode.org/copyright.html#License
package com.ibm.icu.impl.number.formatters;

import java.text.ParseException;

import com.ibm.icu.impl.number.FormatQuantity;
import com.ibm.icu.impl.number.ModifierHolder;
import com.ibm.icu.impl.number.PNAffixGenerator;
import com.ibm.icu.impl.number.modifiers.PositiveNegativeAffixModifier;
import com.ibm.icu.text.DecimalFormatSymbols;

/**
 * The implementation of this class is a thin wrapper around {@link PNAffixGenerator}, a utility
 * used by this and other classes, including {@link CompactDecimalFormat} and {@link Parse}, to
 * efficiently convert from the abstract properties in the property bag to actual prefix and suffix
 * strings.
 */

/**
 * This class is responsible for adding the positive/negative prefixes and suffixes from the decimal
 * format pattern. Properties are set using the following methods:
 *
 * <ul>
 *   <li>{@link IProperties#setPositivePrefix(CharSequence)}
 *   <li>{@link IProperties#setPositiveSuffix(CharSequence)}
 *   <li>{@link IProperties#setNegativePrefix(CharSequence)}
 *   <li>{@link IProperties#setNegativeSuffix(CharSequence)}
 *   <li>{@link IProperties#setPositivePrefixPattern(CharSequence)}
 *   <li>{@link IProperties#setPositiveSuffixPattern(CharSequence)}
 *   <li>{@link IProperties#setNegativePrefixPattern(CharSequence)}
 *   <li>{@link IProperties#setNegativeSuffixPattern(CharSequence)}
 * </ul>
 *
 * If one of the first four methods is used (those of the form <code>setXxxYyy</code>), the value
 * will be interpreted literally. If one of the second four methods is used (those of the form
 * <code>setXxxYyyPattern</code>), locale-specific symbols for the plus sign, minus sign, percent
 * sign, permille sign, and currency sign will be substituted into the string, according to Unicode
 * Technical Standard #35 (LDML) section 3.2.
 *
 * <p>Literal characters can be used in the <code>setXxxYyyPattern</code> methods by using quotes;
 * for example, to display a literal "%" sign, you can set the pattern <code>'%'</code>. To display
 * a literal quote, use two quotes in a row, like <code>''</code>.
 *
 * <p>If a value is set in both a <code>setXxxYyy</code> method and in the corresponding <code>
 * setXxxYyyPattern</code> method, the one set in <code>setXxxYyy</code> takes precedence.
 *
 * <p>For more information on formatting currencies, see {@link CurrencyFormat}.
 *
 * <p>The parameter is taken by reference by these methods into the property bag, meaning that if a
 * mutable object like StringBuilder is passed, changes to the StringBuilder will be reflected in
 * the property bag. However, upon creation of a finalized formatter object, all prefixes and
 * suffixes will be converted to strings and will stop reflecting changes in the property bag.
 */
public class PositiveNegativeAffixFormat {

  public static interface IProperties {

    static CharSequence DEFAULT_POSITIVE_PREFIX = null;

    /** @see #setPositivePrefix */
    public CharSequence getPositivePrefix();

    /**
     * Sets the prefix to prepend to positive numbers. The prefix will be interpreted literally. For
     * example, if you set a positive prefix of <code>p</code>, then the number 123 will be
     * formatted as "p123" in the locale <em>en-US</em>.
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param positivePrefix The CharSequence to prepend to positive numbers.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setPositivePrefixPattern
     */
    public IProperties setPositivePrefix(CharSequence positivePrefix);

    static CharSequence DEFAULT_POSITIVE_SUFFIX = null;

    /** @see #setPositiveSuffix */
    public CharSequence getPositiveSuffix();

    /**
     * Sets the suffix to append to positive numbers. The suffix will be interpreted literally. For
     * example, if you set a positive suffix of <code>p</code>, then the number 123 will be
     * formatted as "123p" in the locale <em>en-US</em>.
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param positiveSuffix The CharSequence to append to positive numbers.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setPositiveSuffixPattern
     */
    public IProperties setPositiveSuffix(CharSequence positiveSuffix);

    static CharSequence DEFAULT_NEGATIVE_PREFIX = null;

    /** @see #setNegativePrefix */
    public CharSequence getNegativePrefix();

    /**
     * Sets the prefix to prepend to negative numbers. The prefix will be interpreted literally. For
     * example, if you set a negative prefix of <code>n</code>, then the number -123 will be
     * formatted as "n123" in the locale <em>en-US</em>. Note that if the negative prefix is left unset,
     * the locale's minus sign is used.
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param negativePrefix The CharSequence to prepend to negative numbers.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setNegativePrefixPattern
     */
    public IProperties setNegativePrefix(CharSequence negativePrefix);

    static CharSequence DEFAULT_NEGATIVE_SUFFIX = null;

    /** @see #setNegativeSuffix */
    public CharSequence getNegativeSuffix();

    /**
     * Sets the suffix to append to negative numbers. The suffix will be interpreted literally. For
     * example, if you set a suffix prefix of <code>n</code>, then the number -123 will be formatted
     * as "-123n" in the locale <em>en-US</em>. Note that the minus sign is prepended by default unless
     * otherwise specified in either the pattern string or in one of the {@link #setNegativePrefix}
     * methods.
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param negativeSuffix The CharSequence to append to negative numbers.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setNegativeSuffixPattern
     */
    public IProperties setNegativeSuffix(CharSequence negativeSuffix);

    static CharSequence DEFAULT_POSITIVE_PREFIX_PATTERN = null;

    /** @see #setPositivePrefixPattern */
    public CharSequence getPositivePrefixPattern();

    /**
     * Sets the prefix to prepend to positive numbers. Locale-specific symbols will be substituted
     * into the string according to Unicode Technical Standard #35 (LDML).
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param positivePrefixPattern The CharSequence to prepend to positive numbers after locale
     *     symbol substitutions take place.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setPositivePrefix
     */
    public IProperties setPositivePrefixPattern(CharSequence positivePrefixPattern);

    static CharSequence DEFAULT_POSITIVE_SUFFIX_PATTERN = null;

    /** @see #setPositiveSuffixPattern */
    public CharSequence getPositiveSuffixPattern();

    /**
     * Sets the suffix to append to positive numbers. Locale-specific symbols will be substituted
     * into the string according to Unicode Technical Standard #35 (LDML).
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param positiveSuffixPattern The CharSequence to append to positive numbers after locale
     *     symbol substitutions take place.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setPositiveSuffix
     */
    public IProperties setPositiveSuffixPattern(CharSequence positiveSuffixPattern);

    static CharSequence DEFAULT_NEGATIVE_PREFIX_PATTERN = null;

    /** @see #setNegativePrefixPattern */
    public CharSequence getNegativePrefixPattern();

    /**
     * Sets the prefix to prepend to negative numbers. Locale-specific symbols will be substituted
     * into the string according to Unicode Technical Standard #35 (LDML).
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param negativePrefixPattern The CharSequence to prepend to negative numbers after locale
     *     symbol substitutions take place.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setNegativePrefix
     */
    public IProperties setNegativePrefixPattern(CharSequence negativePrefixPattern);

    static CharSequence DEFAULT_NEGATIVE_SUFFIX_PATTERN = null;

    /** @see #setNegativeSuffixPattern */
    public CharSequence getNegativeSuffixPattern();

    /**
     * Sets the suffix to append to negative numbers. Locale-specific symbols will be substituted
     * into the string according to Unicode Technical Standard #35 (LDML).
     *
     * <p>For more information on prefixes and suffixes, see {@link PositiveNegativeAffixFormat}.
     *
     * @param negativeSuffixPattern The CharSequence to append to negative numbers after locale
     *     symbol substitutions take place.
     * @return The property bag, for chaining.
     * @see PositiveNegativeAffixFormat
     * @see #setNegativeSuffix
     */
    public IProperties setNegativeSuffixPattern(CharSequence negativeSuffixPattern);

    static boolean DEFAULT_ALWAYS_SHOW_PLUS_SIGN = false;

    /** @see #setAlwaysShowPlusSign */
    public boolean getAlwaysShowPlusSign();

    /**
     * Sets whether to always display of a plus sign on positive numbers.
     *
     * <p>If the location of the negative sign is specified by the decimal format pattern (or by the
     * negative prefix/suffix pattern methods), a plus sign is substituted into that location, in
     * accordance with Unicode Technical Standard #35 (LDML) section 3.2.1. Otherwise, the plus sign
     * is prepended to the number. For example, if the decimal format pattern <code>#;#-</code> is
     * used, then formatting 123 would result in "123+" in the locale <em>en-US</em>.
     *
     * <p>This method should be used <em>instead of</em> setting the positive prefix/suffix. The
     * behavior is undefined if alwaysShowPlusSign is set but the positive prefix/suffix already
     * contains a plus sign.
     *
     * @param alwaysShowPlusSign Whether positive numbers should display a plus sign.
     * @return The property bag, for chaining.
     */
    public IProperties setAlwaysShowPlusSign(boolean alwaysShowPlusSign);
  }

  public static PositiveNegativeAffixModifier getInstance(DecimalFormatSymbols symbols, IProperties properties)
      throws ParseException {
    PNAffixGenerator pnag = PNAffixGenerator.getThreadLocalInstance();
    PNAffixGenerator.Result result = pnag.getModifiers(symbols, properties);
    return new PositiveNegativeAffixModifier(result.positive, result.negative);
  }

  // TODO: Investigate static interface methods (Java 8 only?)
  public static void apply(
      FormatQuantity input,
      ModifierHolder mods,
      DecimalFormatSymbols symbols,
      IProperties properties)
      throws ParseException {
    PNAffixGenerator pnag = PNAffixGenerator.getThreadLocalInstance();
    PNAffixGenerator.Result result = pnag.getModifiers(symbols, properties);
    if (input.isNegative()) {
      mods.add(result.negative);
    } else {
      mods.add(result.positive);
    }
  }
}
