package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.itk.simple.Image;
import org.itk.simple.PixelIDValueEnum;
import org.itk.simple.SimpleITK;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.*;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKBSplineDecomposition")
public class SimpleITKBSplineDecomposition extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number spline_order";
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKBSplineDecomposition(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asInteger(args[2])));
        return result;
    }

    public static synchronized boolean simpleITKBSplineDecomposition(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Integer spline_order) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);
        itk_input = SimpleITK.cast(itk_input, PixelIDValueEnum.sitkFloat32);

        // apply SimpleITK BinomialBlur
        Image itk_output = SimpleITK.bSplineDecomposition(itk_input, spline_order);

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.copy(result, output);

        // clean up
        result.close();

        return true;
    }


    @Override
    public String getDescription() {
        return "Apply SimpleITKs BSpline Decomposition filter to an image.\n\n" +
                "Spline order must be within 0... 5" +
                "See also: https://simpleitk.org/doxygen/latest/html/sitkBSplineDecompositionImageFilter_8h.html";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Filter";
    }
}
