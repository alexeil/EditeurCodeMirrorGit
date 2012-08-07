package org.kevoree.library.javase.webserver.collaborationToolsBasics.client.Forms;

import com.google.gwt.user.client.ui.HTML;

/**
 * Created with IntelliJ IDEA.
 * User: tboschat
 * Date: 8/3/12
 * Time: 8:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class UploadDragAndDrop extends HTML {

    public UploadDragAndDrop() {

        this.setHTML("   <!-- The file upload form used as target for the file upload widget -->\n" +
                "    <form id=\"fileupload\" action=\"ihmcodemirror/upload\" method=\"POST\" enctype=\"multipart/form-data\">\n" +
                "        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->\n" +
                "        <div class=\"row fileupload-buttonbar\">\n" +
                "            <div class=\"span7\">\n" +
                "                <!-- The fileinput-button span is used to style the file input field as button -->\n" +
                "                <span class=\"btn btn-success fileinput-button\">\n" +
                "                    <i class=\"icon-plus icon-white\"></i>\n" +
                "                    <span>Add files...</span>\n" +
                "                    <input type=\"file\" name=\"files[]\" multiple>\n" +
                "                </span>\n" +
                "                <button type=\"submit\" class=\"btn btn-primary start\">\n" +
                "                    <i class=\"icon-upload icon-white\"></i>\n" +
                "                    <span>Start upload</span>\n" +
                "                </button>\n" +
                "                <button type=\"reset\" class=\"btn btn-warning cancel\">\n" +
                "                    <i class=\"icon-ban-circle icon-white\"></i>\n" +
                "                    <span>Cancel upload</span>\n" +
                "                </button>\n" +
                "                <button type=\"button\" class=\"btn btn-danger delete\">\n" +
                "                    <i class=\"icon-trash icon-white\"></i>\n" +
                "                    <span>Delete</span>\n" +
                "                </button>\n" +
                "                <input type=\"checkbox\" class=\"toggle\">\n" +
                "            </div>\n" +
                "            <!-- The global progress information -->\n" +
                "            <div class=\"span5 fileupload-progress fade\">\n" +
                "                <!-- The global progress bar -->\n" +
                "                <div class=\"progress progress-success progress-striped active\" role=\"progressbar\" aria-valuemin=\"0\" aria-valuemax=\"100\">\n" +
                "                    <div class=\"bar\" style=\"width:0%;\"></div>\n" +
                "                </div>\n" +
                "                <!-- The extended global progress information -->\n" +
                "                <div class=\"progress-extended\">&nbsp;</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <!-- The loading indicator is shown during file processing -->\n" +
                "        <div class=\"fileupload-loading\"></div>\n" +
                "        <br>\n" +
                "        <!-- The table listing the files available for upload/download -->\n" +
                "        <table role=\"presentation\" class=\"table table-striped\"><tbody class=\"files\" data-toggle=\"modal-gallery\" data-target=\"#modal-gallery\"></tbody></table>\n" +
                "    </form>\n" +
                "    \n" +
                "</div>\n" +
                "<!-- modal-gallery is the modal dialog used for the image gallery -->\n" +
                "<div id=\"modal-gallery\" class=\"modal modal-gallery hide fade\" data-filter=\":odd\">\n" +
                "    <div class=\"modal-header\">\n" +
                "        <a class=\"close\" data-dismiss=\"modal\">&times;</a>\n" +
                "        <h3 class=\"modal-title\"></h3>\n" +
                "    </div>\n" +
                "    <div class=\"modal-body\"><div class=\"modal-image\"></div></div>\n" +
                "    <div class=\"modal-footer\">\n" +
                "        <a class=\"btn modal-download\" target=\"_blank\">\n" +
                "            <i class=\"icon-download\"></i>\n" +
                "            <span>Download</span>\n" +
                "        </a>\n" +
                "        <a class=\"btn btn-success modal-play modal-slideshow\" data-slideshow=\"5000\">\n" +
                "            <i class=\"icon-play icon-white\"></i>\n" +
                "            <span>Slideshow</span>\n" +
                "        </a>\n" +
                "        <a class=\"btn btn-info modal-prev\">\n" +
                "            <i class=\"icon-arrow-left icon-white\"></i>\n" +
                "            <span>Previous</span>\n" +
                "        </a>\n" +
                "        <a class=\"btn btn-primary modal-next\">\n" +
                "            <span>Next</span>\n" +
                "            <i class=\"icon-arrow-right icon-white\"></i>\n" +
                "        </a>\n" +
                "    </div>\n" +
                "</div>");
    }
}