package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
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

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKInverseDeconvolution")
public class SimpleITKInverseDeconvolution extends AbstractSimpleITKCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, Image input_psf, ByRef Image destination, Number kernelZeroMagnitudeThreshold, Boolean normalize";
    }


    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, null, 0.01, true};
    }

    @Override
    public boolean executeCL() {
        boolean result = runAndCatch(() -> simpleITKInverseDeconvolution(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), (ClearCLBuffer) (args[2]), asFloat(args[3]), asBoolean(args[4])));
        return result;
    }

    public static boolean simpleITKInverseDeconvolution(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer input_psf, ClearCLBuffer output, Float kernelZeroMagnitudeThreshold, Boolean normalize ) {
        // convert to ITK
        Image itk_input = clijToITK(clij2, input);
        Image itk_input_psf = clijToITK(clij2, input_psf);
        itk_input = SimpleITK.cast(itk_input, PixelIDValueEnum.sitkFloat32);
        itk_input_psf = SimpleITK.cast(itk_input_psf, PixelIDValueEnum.sitkFloat32);

        // apply Simple Inverse Deconvolution
        Image itk_output = SimpleITK.inverseDeconvolution(itk_input, itk_input_psf, kernelZeroMagnitudeThreshold, normalize);

        // push result back
        ClearCLBuffer result = itkToCLIJ(clij2, itk_output);

        // save it in the right place
        clij2.copy(result, output);

        // clean up
        result.close();

        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.Float);
    }

    @Override
    public String getDescription() {
        return "Apply SimpleITKs Inverse Deconvolution to an image.\n\n" +
                "See also: https://itk.org/Doxygen/html/classitk_1_1InverseDeconvolutionImageFilter.html";
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
