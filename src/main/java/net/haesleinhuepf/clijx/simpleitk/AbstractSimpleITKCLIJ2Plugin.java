package net.haesleinhuepf.clijx.simpleitk;

import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;

abstract public class AbstractSimpleITKCLIJ2Plugin extends AbstractCLIJ2Plugin implements HasAuthor, HasLicense {
    @Override
    public String getAuthorName() {
        return "Robert Haase, based on work by Bradley Lowekamp and the SimpleITK and ITK teams";
    }

    @Override
    public String getLicense() {
        return "This plugin is licensed BSD3, see:\n" +
                "https://github.com/clij/clijx-assistant-simpleitk/blob/master/license.txt\n" +
                "The underlying SimpleITK librariy is Apache 2 licensed:\n" +
                "https://github.com/SimpleITK/SimpleITK/blob/master/LICENSE";
    }
}
