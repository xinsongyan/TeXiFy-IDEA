package nl.rubensten.texifyidea.inspections.latex

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import nl.rubensten.texifyidea.file.LatexFileType
import nl.rubensten.texifyidea.index.LatexCommandsIndex
import nl.rubensten.texifyidea.insight.InsightGroup
import nl.rubensten.texifyidea.inspections.TexifyInspectionBase

/**
 * @author Ruben Schellekens
 */
open class LatexMissingDocumentclassInspection : TexifyInspectionBase() {

    override fun getInspectionGroup() = InsightGroup.LATEX

    override fun getDisplayName() = "Missing documentclass"

    override fun getInspectionId() = "MissingDocumentclass"

    override fun inspectFile(file: PsiFile, manager: InspectionManager, isOntheFly: Boolean): List<ProblemDescriptor> {
        val descriptors = descriptorList()

        // Workaround, because .sty files also return LatexFileType.INSTANCE
        if (file.virtualFile.extension != LatexFileType.defaultExtension) {
            return descriptors
        }

        val commands = LatexCommandsIndex.getItemsInFileSet(file)
        val hasDocumentclass = commands.stream()
                .filter { cmd -> cmd.name == "\\documentclass" }
                .count() > 0

        if (!hasDocumentclass) {
            descriptors.add(
                    manager.createProblemDescriptor(
                            file,
                            "Document doesn't contain a \\documentclass command.",
                            InspectionFix(),
                            ProblemHighlightType.GENERIC_ERROR,
                            isOntheFly
                    )
            )
        }

        return descriptors
    }

    private class InspectionFix : LocalQuickFix {

        override fun getFamilyName() = "Add \\documentclass{article}"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val psiElement = descriptor.psiElement
            val file = psiElement.containingFile
            val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return

            document.insertString(0, "\\documentclass{article}\n")
        }
    }
}
