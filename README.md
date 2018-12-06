# EmbroideryIO
Java/Android library for input/output of Embroidery file types.

Ensure jitpack.io in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency.
```
  dependencies {
	        implementation 'com.github.EmbroidePy:EmbroideryIO:0.0.1'
	}
  ```
  
And you now how effective, correct, and properly working EmbroideryIO for java/android.

If being used for Oracle Java, there is a single character in the EmbMatrix file that needs to be changed to switch between the andoid Matrix class and the Java AffineTransformation class.

Introduction
---

Everything is a honest transcription of pyembroidery ( https://github.com/EmbroidePy/pyembroidery ) and uses the same scheme internally, so most of the functionality from there is present here with minor changes due to language differences.  It does not include format-wise it does not include gcode, png, svg, or txt.

Any suggestions or comments please raise an issue on the github.

EmbroiderIO was ported from pyembroidery, both done by the same original author with all projects in mind. It includes a lot of higher level and middle level pattern composition abilities, and should accounts for any knowable error. It should be highly robust with a simple api so as to be reasonable for *any* java embroidery project.

It should be complex enough to go very easily from points to stitches, fine grained enough to let you control everything, and good enough that you shouldn't want to.


Philosophy
---
EmbroideryIO will always attempt to minimize information loss. Embroidery reading and writing, the exporting and importing of embroidery files, is always lossy. If there is information in a file, it is within the purview of the project to read that information and provide it to the user. If information can be written to a file, it is within the purview of the project to write that information to the file or provide means by which that can be done.

* Low level commands: Those commands actually found in binary encoded embroidery files.
    * Low level commands will be transcribed and preserved in their exact order, unless doing so will cause an error.
* Middle level commands: Useful ways of thinking about blocks of low level commands. Commands which describe the way the low level commands are encoded, but are not themselves commands executed by embroidery machines.
    * Middle level commands will be helpful and converted to low-level commands during writing events.
    * These will often be context sensitive converting to slightly different low level commands depending on intended writer, or encoder settings.
* High level commands: Conversion of shapes and fills into useful structures, patterning within stitches, modifiers of structures.
    * High level commands will not exist within this project.


Features
---
* Writes 10 formats
* Reads 40 formats
* Supports rarer embroidery commands like FAST, SLOW, and SEQUIN_EJECT

Formats:
---
EmbroideryIO will write:
* .pes
* .dst
* .exp
* .jef
* .vp3
* .u01
* .pec
* .xxx
* .csv
* .emm

Pyembroidery will read:
* .pes
* .dst
* .exp
* .jef
* .vp3
* .10o
* .100
* .bro
* .dat (barudan & sunstar)
* .dsb
* .dsz
* .emd
* .exy
* .fxy
* .gt
* .inb
* .jpx
* .ksm
* .max
* .mit
* .new
* .pcd
* .pcm
* .pcq
* .pcs
* .pec
* .phb
* .phc
* .sew
* .shv
* .stc
* .stx
* .tap
* .tbf
* .u01
* .xxx
* .zxy
* .csv
* .emm

API
---

The parameters currently have recognized values for:
* `max_stitch`
* `max_jump`
* `full_jump`
* `round`
* `needle_count`
* `thread_change_command`
* `long_stitch_contingency`
* `sequin_contingency`
* `tie_on`
* `tie_off`
* `explicit_trim`
* `writes_speeds`
* `translate`
* `scale`
* `rotate`
* `encode`

The max_stitch, max_jump, full_jump, round, needle_count, thread_change_command, and sequin_contingency properties are appended by default depending on the format being written. For example, DST files support a maximum stitch length of 12.1mm, and this is set automatically. If you set these explicitly, (eg:`"max_stitch", 2000`) they will override format values. If overridden or if you disable the encoder (`"encode", false`) and the pattern contains values that cannot be accounted for by the reader/writer, it may raise and uncaught issue.

`translate`, `scale` and `rotate` occur in that order. If you need finer grain control over these they can be modified on the fly with middle-level commands. `pattern.add_command(MATRIX_TRANSLATE, 40, 40)`

`long_stitch_contingency` sets the contingency protocol for when a stitch is longer than the format can encode and how to deal with that event.

`sequin_contingency` sets the contingency protocol for when sequins exist in a pattern. By default this tends to be `CONTINGENCY_SEQUIN_JUMP` converting whatever sequins are in the data into jumps (this can sometimes be restored on various embroidery machines). For .dst files it uses `CONTINGENCY_SEQUIN_UTILIZE` as the format is able to fully encode sequin data. You may also use `CONTINGENCY_SEQUIN_REMOVE` to simply remove the commands completely as if they never existed or `CONTINGENCY_SEQUIN_STITCH` which converts the sequin stitches to stitches. This will look better, but is more lossy.

`tie_on` sets the contingency protocol for when a tie_on is needed. This can either be `CONTINGENCY_TIE_ON_THREE_SMALL` which uses three small stitches to tie on the thread or `CONTINGENCY_TIE_ON_NONE` which does not perform a tie_on.

`tie_off` sets the contingency protocol for when a tie_off is needed. This can either be `CONTINGENCY_TIE_OFF_THREE_SMALL` which uses three small stitches to tie off the thread or `CONTINGENCY_TIE_OFF_NONE` which does not perform a tie_off.

Explicitly calling TIE_ON or TIE_OFF within the command sequence performs the set contingency so if this is set to `CONTINGENCY_TIE_OFF_NONE` these will perform no action. These could be modified on the fly by adding a command for `CONTINGENCY_TIE_OFF_THREE_SMALL` to toggle the value on the fly.

`explicit_trim` sets whether the encoder should overtly include a trim before color change event or not. Default is False. Setting this to True will include a trim if we are going to perform a thread-change action.

Middle-Level Commands:
---

The middle-level commands, as they currently stand:
* SET_CHANGE_SEQUENCE - Sets the thread change sequence according to the encoded values. Setting the needle, thread-color, and order of where this occurs. See Thread Changes for more info.
* SEQUENCE_BREAK - Break between stitches. Inserts a trim and jumps to the next stitch in the sequence.
* COLOR_BREAK - Breaks between stitches. Changes to the next color (unless called before anything was stitched)
* FRAME_EJECT(x,y) - Breaks the stitches, jumps to the given location, performs a stop, then goes to next stitch accordingly.
* STITCH_BREAK - Next location is jumped to. Existing jumps are reallocated.
* MATRIX_TRANSLATE(tx,ty) - Applies an inline translation shift for the encoder. It will treat all future stitches translated from here.
* MATRIX_SCALE_ORIGIN(sx,sy) - Applies an inline scale shift. It will scale by that factor for future stitches. Against the origin (0,0)
* MATRIX_ROTATE_ORIGIN(r) - Applies an inline rotateion shift. It will rotate by that factor for future stitches (in degrees). Against the origin (0,0)
* MATRIX_SCALE(sx,sy) - Applies an inline scale shift. It will scale by that factor for future stitches. Scaling based on current point.
* MATRIX_ROTATE(r) - Applies an inline rotateion shift. It will rotate by that factor for future stitches (in degrees).
* MATRIX_RESET - Resets the affine transformation matrix.
* OPTION_MAX_STITCH_LENGTH(x) - Sets the max stitch length on the fly.
* OPTION_MAX_JUMP_LENGTH(x) - Sets the max jump length on the fly.
* OPTION_EXPLICIT_TRIM - (Default) includes trim command before color-change command explicitly. 
* OPTION_IMPLICIT_TRIM - Sets trim to be implied by the color-change event.
* CONTINGENCY_TIE_ON_THREE_SMALL - Enables Tie_on on the fly.
* CONTINGENCY_TIE_ON_NONE - Enables Tie_off on the fly.
* CONTINGENCY_TIE_OFF_THREE_SMALL - Disables Tie_on on the fly.
* CONTINGENCY_TIE_OFF_NONE - Disables Tie_off on the fly.
* SEW_TO - STITCH but with forced CONTINGENCY_SEW_TO
* NEEDLE_AT - STITCH but with forced CONTINGENCY_JUMP_NEEDLE
* CONTINGENCY_LONG_STITCH_NONE - Disables long stitch contingency encoding.
* CONTINGENCY_LONG_STITCH_JUMP_NEEDLE - Sets, long stitch contingency to jump the needle to the new position.
* CONTINGENCY_LONG_STITCH_SEW_TO - Sets, long stitch contingency to sew to the new position with interpolated stitches.
* CONTINGENCY_SEQUIN_UTILIZE - sets the equin contingency to use the sequin information.
* CONTINGENCY_SEQUIN_JUMP - Sets the sequin contingency to call the sequins jumps.
* CONTINGENCY_SEQUIN_STITCH - Sets the sequin contingency to call the sequins stitches.
* CONTINGENCY_SEQUIN_REMOVE - Sets the sequin contingency to remove the commands completely.

Note: these do not need to have a 1 to 1 conversion to stitches. Many have 1 to 0 and trigger changes in states for the encoder, or the matrix being used to filter the locations, or specific higher level commands.

The could can be made to do a lot at the encoder level. If something is needed and within scope of the project, raise an issue.

---

COLOR_BREAK and SEQUENCE_BREAK:

The main two middle-level commands simply serve as dividers for series of stitches.
* pattern.command(COLOR_BREAK)
* (add a bunch of stitches)
* pattern.command(SEQUENCE_BREAK)
* (add a bunch of stitches)
* pattern.command(COLOR_BREAK)
* (add a bunch of stitches)
* pattern.command(SEQUENCE_BREAK)

The encoder will by default ignore any COLOR_BREAK that occurs before any stitches have been put down, or sequence or color breaks would occur after all stitching has happened. So you don't have to worry about the order you put them in. They work expressly as breaks that divide one block of stitches from another, and gives information as to whether this change also requires we use a new color.

STITCH_BREAK

Stitch break is only needed for reallocating jumps. It requires that the long stitch contingency is needle_to for the next stitch and any existing jumps directly afterwards are ignored. This causes the jump sequences to reallocate. If an existing jump sequence exists because it was loaded from a file and fed into a write routine. The write routine may only seek a contingency for the long jumps by providing extra subdivisions, because low level commands are only tweaked if a literal transcription would cause errors. However, calling pattern.get_pattern_merge_jumps() returns a pattern with all sequences of JUMP replaced with a single STITCH_BREAK command which is middle level and converted by the encoder into a series of jumps produced by the encoder rather than directly transcribed from their current sequence.

Stitch Contingency
---
The encoder needs to decide what to do when a stitch is too long. The current modes here are:
* CONTINGENCY_NEEDLE_JUMP (default)
* CONTINGENCY_SEW_TO
* CONTINGENCY_NONE

When a stitch is beyond max_stitch (whether set by the format or by the user) it must deal with this event, however opinions differ as to how what a stitch beyond the maximum should do. If it is your intent that STITCH means SEW_TO this location then setting the stitch contingency to SEW_TO will create a series of stitches until we get to the end location. If you use the command SEW_TO this overtly works like a stitch with CONTINGENCY_SEW_TO. Likewise NEEDLE_AT is the STITCH flavor that jumps to to the end location and then stitches. If you set CONTINGENCY_NONE then no contingency method is used, long stitches are simply fed to the writer as they appear which may throw an error or crash.

Sequin Contingency
---
The enconder needs to decide what to do when there are sequins in a pattern. The current modes here are:
* CONTINGENCY_SEQUIN_UTILIZE - sets the equin contingency to use the sequin information.
* CONTINGENCY_SEQUIN_JUMP - Sets the sequin contingency to call the sequins jumps.
* CONTINGENCY_SEQUIN_STITCH - Sets the sequin contingency to call the sequins stitches.
* CONTINGENCY_SEQUIN_REMOVE - Sets the sequin contingency to remove the commands completely.

Sequins being written into files that do not support sequins can go several ways, the two typical methods are JUMP and STITCH, this means to replace the SEQUIN_EJECTs with JUMP. This will allow some machines to manually enable sequins for a particular section and interpret the JUMPs as stitches. It is known that some Barudan machines have this ability. The other typical mode is STITCH which will preserve viewable structure of the underlying pattern while destroying the information of where the JUMPs were. With the JUMPs some data will appear to be corrupted, with STITCHes the data will look correct except without the sequins but the information is lost and not recoverable. REMOVE is given for completeness, but it calls all SEQUIN_EJECT commands NO OPERATIONS as if they don't appear in the pattern at all.

Tie On / Tie Off Contingency
---
While there's only NONE, and THREE_SMALL for contingencies here, both the tie-on and tie-off contingencies are setup to be forward compatabile with various other potential tie-on and tie-off methods.

Units
---
* The core units are 1/10th mm. This is what 1 refers to within most formats, and internally within pyembroidery itself. You are entirely permitted to use floating point numbers. When writing to a format, fractional values will be lost, but this shall happen in such a way to avoid the propagation of error. Relative stitches from position ( 0.0,  0.31 ) of (+5.4, +5.4), (+5.4, +5,4), (+5.4, +5,4) should encode as changes of 5,6 6,5 5,6. Taking the relative distance in the format as the integer change from the last integer position to the new one, maintaining a position as close to the absolute position as possible. All fractional values are considered significant. 

In some read formats the formats themselves have a slightly different unit systems such as .PCD or .MIT these alternative units will be presented seemlessly as 1/10th mm units.

Core Command Ordering
---
Stitch is taken to mean move_position(x,y), needle_strike. Jump is taken to mean move_position(x,y), block_needle_bar. In those orders.
If a format takes stitch to mean needle_strike, move_position(x,y) in that order. The encoder will may insert an extra jump in to avoid stitching an unwanted element. These differences matter, and are accounted for by things like FULL_JUMP in places, and within the formats. However, within the pattern the understanding should be consistently be taken as displace then operation.

Note: This is true for sequin_eject too. DST files are the only currently supported format with sequins and they use dx,dy then command. But, note the sequin is ejected at the destination of the dx dy. It will move, then sequin_eject this is the assumed order. It is also the DST order.

So if write your own pattern and you intend to stitch at the origin and then go somewhere you must `stitch, 0, 0` then `stitch, x, y` if you start by stitching somewhere at x, y. It may insert jump stitches to get you to that location, then stitch at that location.

Coordinate System
---
Fundamentally this project stores the positions such that the +y direction is down and -y is up (when viewed horizontally) with +x right and -x left. This is consistent with most modern graphics coordinate systems, but this is different from how these values are stored within embroidery formats. pyembroidery reads by flipping the y-axis, and writes by flipping the y-axis (except for SVG which uses the same coordinate system). This allows for seemless reading, writing, and interfacing. The flips occur at the level of the format readers and writers and is not subject to encoding. However encoding with scale of (1, -1) would invert this during the encoding. All patterns are stored such that `top` is in the -y direction and `bottom` is in the +y direction.

All patterns start at the origin point (0,0). In keeping with the philosophy the absolute positioning of the data is maintained sometimes this means it an offcenter pattern will move from the origin to an absolute position some distance from the origin. While this preserves information, it might also not be entirely expected at times. This `pattern.move_center_to_origin()` will lose that information and center the pattern at the origin.

---

This code is based on EmbroidePy/pyembroidery Python code,
This code is based on Embroidermodder/MobileViewer Java code,
Which in turn is based on Embroidermodder/libembroidery C++ code.

It is also, used within the Embroidermodder/MobileViewer java code, so it is it's own grandfather.

Thanks to,
* The Embroidermodder Team
* Josh Varga
* Jonathan Greig redteam316
* fabriciocouto
* frno7
* Trever Adams
* Rudolfo @ http://www.achatina.de/sewing/main/TECHNICL.HTM
* wwderw
* Purple-bobby
* Jason Weiler
* And the countless other people who put forward good works in figuring out these formats, and those who may yet do so. 

Also, a thanks goes out to Daniel K. Schneider for his Edutech Wiki (https://edutechwiki.unige.ch/en/Computerized_embroidery), it was one of the first good sources for embroidery and open source embroidery information on the web, and is host to the formats project (https://edutechwiki.unige.ch/en/Embroidery_format) which is a sister project to this trying to write the documentation for some of the formats to help people going forward.

