package io.lumkit.lint.toolkit.terminal.settings

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb
import com.jediterm.terminal.HyperlinkStyle.HighlightMode
import com.jediterm.terminal.TerminalColor
import com.jediterm.terminal.TextStyle
import com.jediterm.terminal.emulator.ColorPalette
import com.jediterm.terminal.model.LinesBuffer
import com.jediterm.terminal.model.TerminalTypeAheadSettings
import com.jediterm.terminal.ui.AwtTransformers
import com.jediterm.terminal.ui.TerminalActionPresentation
import com.jediterm.terminal.ui.isMacOS
import com.jediterm.terminal.ui.settings.SettingsProvider
import java.awt.Color
import java.awt.Font
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class LintToolkitSettingsProvider(
    private val colorScheme: ColorScheme
) : SettingsProvider {
    override fun getOpenUrlActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation("Open as URL", emptyList())
    }

    override fun getCopyActionPresentation(): TerminalActionPresentation {
        val keyStroke = if (isMacOS()
        ) KeyStroke.getKeyStroke(
            KeyEvent.VK_C,
            InputEvent.META_DOWN_MASK
        ) // CTRL + C is used for signal; use CTRL + SHIFT + C instead
        else KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK)
        return TerminalActionPresentation("Copy", keyStroke)
    }

    override fun getPasteActionPresentation(): TerminalActionPresentation {
        val keyStroke = if (isMacOS()
        ) KeyStroke.getKeyStroke(
            KeyEvent.VK_V,
            InputEvent.META_DOWN_MASK
        ) // CTRL + V is used for signal; use CTRL + SHIFT + V instead
        else KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK)
        return TerminalActionPresentation("Paste", keyStroke)
    }

    override fun getClearBufferActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Clear Buffer", if (isMacOS()
            ) KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.META_DOWN_MASK)
            else KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK)
        )
    }

    override fun getPageUpActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Page Up",
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.SHIFT_DOWN_MASK)
        )
    }

    override fun getPageDownActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Page Down",
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.SHIFT_DOWN_MASK)
        )
    }

    override fun getLineUpActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Line Up", if (isMacOS()
            ) KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.META_DOWN_MASK)
            else KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK)
        )
    }

    override fun getLineDownActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Line Down", if (isMacOS()
            ) KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.META_DOWN_MASK)
            else KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK)
        )
    }

    override fun getFindActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation(
            "Find", if (isMacOS()
            ) KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.META_DOWN_MASK)
            else KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)
        )
    }

    override fun getSelectAllActionPresentation(): TerminalActionPresentation {
        return TerminalActionPresentation("Select All", emptyList())
    }

    override fun getTerminalColorPalette(): ColorPalette {
        val colors = arrayOf(
            com.jediterm.core.Color(colorScheme.onBackground.toArgb()),  //Black
            com.jediterm.core.Color(0x800000),  //Red
            com.jediterm.core.Color(0x008000),  //Green
            com.jediterm.core.Color(0x808000),  //Yellow
            com.jediterm.core.Color(0x000080),  //Blue
            com.jediterm.core.Color(0x800080),  //Magenta
            com.jediterm.core.Color(0x008080),  //Cyan
            com.jediterm.core.Color(0xc0c0c0),  //White
            //Bright versions of the ISO colors
            com.jediterm.core.Color(0x808080),  //Black
            com.jediterm.core.Color(colorScheme.error.toArgb()),  //Red
            com.jediterm.core.Color(0x6DBC66),  //Green
            com.jediterm.core.Color(colorScheme.primary.toArgb()),  //Yellow
            com.jediterm.core.Color(0x4682b4),  //Blue
            com.jediterm.core.Color(0xff00ff),  //Magenta
            com.jediterm.core.Color(0x00ffff),  //Cyan
            com.jediterm.core.Color(colorScheme.background.toArgb()),  //White
        )
        return object : ColorPalette() {
            override fun getForegroundByColorIndex(colorIndex: Int): com.jediterm.core.Color {
                return colors[colorIndex]
            }
            override fun getBackgroundByColorIndex(colorIndex: Int): com.jediterm.core.Color {
                return colors[colorIndex]
            }
        }
    }

    override fun getTerminalFont(): Font {
        return Font("Consolas", Font.PLAIN, 14)
    }

    override fun getTerminalFontSize(): Float {
        return 14f
    }

    override fun getSelectionColor(): TextStyle {
        return TextStyle(TerminalColor.WHITE, TerminalColor.rgb(82, 109, 165))
    }

    override fun getFoundPatternColor(): TextStyle {

        return TextStyle(TerminalColor.BLACK, TerminalColor.rgb(255, 255, 0))
    }

    override fun getHyperlinkColor(): TextStyle {
        return TextStyle(AwtTransformers.fromAwtToTerminalColor(Color.BLUE), TerminalColor.WHITE)
    }

    override fun getHyperlinkHighlightingMode(): HighlightMode {
        return HighlightMode.HOVER
    }

    override fun useInverseSelectionColor(): Boolean {
        return true
    }

    override fun copyOnSelect(): Boolean {
        return emulateX11CopyPaste()
    }

    override fun pasteOnMiddleMouseClick(): Boolean {
        return emulateX11CopyPaste()
    }

    override fun emulateX11CopyPaste(): Boolean {
        return false
    }

    override fun useAntialiasing(): Boolean {
        return true
    }

    override fun maxRefreshRate(): Int {
        return 50
    }

    override fun audibleBell(): Boolean {
        return true
    }

    override fun enableMouseReporting(): Boolean {
        return true
    }

    override fun caretBlinkingMs(): Int {
        return 505
    }

    override fun scrollToBottomOnTyping(): Boolean {
        return true
    }

    override fun DECCompatibilityMode(): Boolean {
        return true
    }

    override fun forceActionOnMouseReporting(): Boolean {
        return false
    }

    override fun getBufferMaxLinesCount(): Int {
        return LinesBuffer.DEFAULT_MAX_LINES_COUNT
    }

    override fun altSendsEscape(): Boolean {
        return true
    }

    override fun ambiguousCharsAreDoubleWidth(): Boolean {
        return false
    }

    override fun getTypeAheadSettings(): TerminalTypeAheadSettings {
        return TerminalTypeAheadSettings.DEFAULT
    }

    override fun sendArrowKeysInAlternativeMode(): Boolean {
        return true
    }
}