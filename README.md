Android-FloatingLabel
=====================

Problem with previous libraries I found, for floating label, was that EditText was always incompletely overloaded. (EditText is a object with so many options)

With this method, a custom view simply wraps the EditText, leaving the dev to utilize a normal EditText's attributes.

I am going to try add some more features for the floating label custom view.


Available Attributes
====

These attributes are equivalent to the attributes used for textViews

**Text Appearance Attributes**

  *flTextAppearance
  *flFontFamily
  *flTypeface
  *flTextStyle
  *flTextColor
  *flTextSize
  *flShadowColor
  *flShadowDx
  *flShadowDy
  *flShadowRadius
  *flTextAllCaps

  *flPadding
  *flPaddingLeft
  *flPaddingRight
  *flPaddingTop
  *flPaddingBottom
  *flGapSize
  *flAnimationDuration
  *flGravity
  *flText
  *flScrollHorizontally
  *flEllipsize
  *flIncludeFontPadding
  *flTextScaleX

Original Concept
================

Chris Banes' FloatLabelLayout(Gist) idea where custom view is simply a wrapper.