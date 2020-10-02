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

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKZeroCrossingBasedEdgeDetection")
public class SimpleITKZeroCrossingBasedEdgeDetection extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number variance, Number maximum_error";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, 1, 0.1};
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKZeroCrossingBasedEdgeDetection(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2]), asFloat(args[3])));
        return result;
    }

    public static synchronized boolean simpleITKZeroCrossingBasedEdgeDetection(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Float variance, Float maximum_error) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);
        itk_input = SimpleITK.cast(itk_input, PixelIDValueEnum.sitkFloat32);

        // apply Simple ITK zero crossing based edge detection
        Image itk_output = SimpleITK.zeroCrossingBasedEdgeDetection(itk_input, variance, (short)1, (short)0, maximum_error);

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
        return "Apply SimpleITKs ZeroCrossingBasedEdgeDetection to an image.\n\n" +
                "See also: https://simpleitk.org/doxygen/latest/html/sitkZeroCrossingBasedEdgeDetectionImageFilter_8h.html";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Binary,Segmentation";
    }
}
