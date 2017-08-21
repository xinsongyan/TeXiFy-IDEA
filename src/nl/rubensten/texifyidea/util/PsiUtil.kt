package nl.rubensten.texifyidea.util

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import nl.rubensten.texifyidea.index.LatexCommandsIndex
import nl.rubensten.texifyidea.psi.LatexBeginCommand
import nl.rubensten.texifyidea.psi.LatexCommands
import nl.rubensten.texifyidea.psi.LatexMathContent
import nl.rubensten.texifyidea.psi.LatexPsiUtil
import kotlin.reflect.KClass

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// PSI ELEMENT ///////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the offset where the psi element ends.
 */
fun PsiElement.endOffset(): Int = textOffset + textLength

/**
 * @see [PsiTreeUtil.getChildrenOfType]
 */
fun <T : PsiElement> PsiElement.childrenOfType(clazz: KClass<T>): Collection<T> = PsiTreeUtil.findChildrenOfType(this, clazz.java)

/**
 * @see [PsiTreeUtil.getParentOfType]
 */
fun <T : PsiElement> PsiElement.parentOfType(clazz: KClass<T>): T? = PsiTreeUtil.getParentOfType(this, clazz.java)

/**
 * Checks if the psi element has a parent of a given class.
 */
fun <T : PsiElement> PsiElement.hasParent(clazz: KClass<T>): Boolean = parentOfType(clazz) != null

/**
 * Checks if the psi element is in math mode or not.
 *
 * @return `true` when the element is in math mode, `false` when the element is in no math mode.
 */
fun PsiElement.inMathMode(): Boolean = hasParent(LatexMathContent::class)

/**
 * @see LatexPsiUtil.getPreviousSiblingIgnoreWhitespace
 */
fun PsiElement.previousSiblingIgnoreWhitespace() = LatexPsiUtil.getPreviousSiblingIgnoreWhitespace(this)

/**
 * @see LatexPsiUtil.getNextSiblingIgnoreWhitespace
 */
fun PsiElement.nextSiblingIgnoreWhitespace() = LatexPsiUtil.getNextSiblingIgnoreWhitespace(this)

/**
 * @see LatexPsiUtil.getAllChildren
 */
fun PsiElement.allChildren(): List<PsiElement> = LatexPsiUtil.getAllChildren(this)

/**
 * @see LatexPsiUtil.getChildren
 */
fun PsiElement.allLatexChildren(): List<PsiElement> = LatexPsiUtil.getChildren(this)

/**
 * Finds the `generations`th parent of the psi element.
 */
fun PsiElement.grandparent(generations: Int): PsiElement? {
    var parent: PsiElement = this
    for (i in 1..generations) {
        parent = parent.parent ?: return null
    }
    return parent
}

/**
 * Checks if the psi element has a (grand) parent that matches the given predicate.
 */
fun PsiElement.hasParentMatching(maxDepth: Int, predicate: (PsiElement) -> Boolean): Boolean {
    var count = 0
    var parent = this.parent
    while (parent != null && parent !is PsiFile) {
        if (predicate(parent)) {
            return true
        }

        parent = parent.parent

        if (count++ > maxDepth) {
            return false
        }
    }

    return false
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// PSI FILE //////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the corresponding document of the PsiFile.
 */
fun PsiFile.document(): Document? = PsiDocumentManager.getInstance(project).getDocument(this)

/**
 * @see [LatexCommandsIndex.getIndexCommands]
 */
fun PsiFile.commandsInFile(): Collection<LatexCommands> = LatexCommandsIndex.getIndexCommands(this)

/**
 * @see [LatexCommandsIndex.getIndexCommandsInFileSet]
 */
fun PsiFile.commandsInFileSet(): Collection<LatexCommands> = LatexCommandsIndex.getIndexCommandsInFileSet(this)

/**
 * @see TexifyUtil.getFileRelativeTo
 */
fun PsiFile.fileRelativeTo(path: String): PsiFile? = TexifyUtil.getFileRelativeTo(this, path)

/**
 * @see TexifyUtil.findLabelsInFileSet
 */
fun PsiFile.labelsInFileSet(): Set<String> = TexifyUtil.findLabelsInFileSet(this)

/**
 * @see TexifyUtil.getReferencedFileSet
 */
fun PsiFile.referencedFiles(): Set<PsiFile> = TexifyUtil.getReferencedFileSet(this)

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// LATEX ELEMENTS ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @see TexifyUtil.getNextCommand
 */
fun LatexCommands.nextCommand(): LatexCommands? = TexifyUtil.getNextCommand(this)

/**
 * @see TexifyUtil.getForcedFirstRequiredParameterAsCommand
 */
fun LatexCommands.forcedFirstRequiredParameterAsCommand(): LatexCommands = TexifyUtil.getForcedFirstRequiredParameterAsCommand(this)

/**
 * @see TexifyUtil.isCommandKnown
 */
fun LatexCommands.isKnown(): Boolean = TexifyUtil.isCommandKnown(this)

/**
 * @see TexifyUtil.isEntryPoint
 */
fun LatexBeginCommand.isEntryPoint(): Boolean = TexifyUtil.isEntryPoint(this)