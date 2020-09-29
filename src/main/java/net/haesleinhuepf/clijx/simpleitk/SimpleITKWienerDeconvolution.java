package net.haesleinhuepf.clijx.simpleitk;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.itk.simple.Image;
import org.itk.simple.SimpleITK;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.clijToITK;
import static net.haesleinhuepf.clijx.simpleitk.CLIJSimpleITKUtilities.itkToCLIJ;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_simpleITKWienerDeconvolution")
public class SimpleITKWienerDeconvolution extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    @Override
    public String getParameterHelpText() {
        return "Image input, Image input_psf, ByRef Image destination, Number noise_variance, Boolean normalize";
    }

    @Override
    public boolean executeCL() {
        boolean result = simpleItkWienerDeconvolution(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), (ClearCLBuffer) (args[2]), asFloat(args[3]), asBoolean(args[4]));
        return result;
    }

    public static boolean simpleItkWienerDeconvolution(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer input_psf, ClearCLBuffer output, Float noise_variance, Boolean normalize ) {

        // convert to ITK
        Image itk_input = clijToITK(clij2, input);
        Image itk_input_psf = clijToITK(clij2, input_psf);

        // apply Simple Wiener Deconvolution
        Image itk_output = SimpleITK.wienerDeconvolution(itk_input, itk_input_psf, noise_variance, normalize);

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
        return "Apply SimpleITKs Wiener Deconvolution to an image.\n\n" +
                "See also: https://itk.org/Doxygen/html/classitk_1_1WienerDeconvolutionImageFilter.html";
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
