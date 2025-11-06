package br.com.jylabtech.getnet_payment_tech.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import com.getnet.posdigital.PosDigital;
import com.getnet.posdigital.PosDigitalRuntimeException;
import com.getnet.posdigital.printer.AlignMode;
import com.getnet.posdigital.printer.FontFormat;
import com.getnet.posdigital.printer.IPrinterCallback;
import com.getnet.posdigital.printer.IPrinterService;
import com.getnet.posdigital.printer.PrinterStatus;

import android.os.RemoteException;
import android.util.Base64;
import java.util.List;
import java.util.Objects;

public class PrintService {
    private final IPrinterService printer = PosDigital.getInstance().getPrinter();

    public Bundle start(List<Bundle> printableContent) {
        try {
            if (PosDigital.getInstance().isInitiated()) {
                validatePrintContent(printableContent);
                printer.init();
                Thread.sleep(5000);
                printer.setGray(5);
                setValuePrint(printableContent);

                IPrinterCallback callback = new IPrinterCallback() {
                    @Override
                    public IBinder asBinder() {
                        return new Binder();
                    }

                    @Override
                    public void onSuccess() {
                        System.out.println("Impressão concluída com sucesso!");
                    }

                    @Override
                    public void onError(int errorCode) {
                        System.out.println("Impressão não concluída");
                    }
                };

                printer.printAndRemovePaper(callback);
                return generateResponse(printer.getStatus());
            } else {
                return generateErrorResponse("Instance of PosDigital not initialized");
            }
        } catch (PosDigitalRuntimeException | IllegalArgumentException e) {
            return generateErrorResponse(e.getMessage());
        } catch (Exception e) {
            return generateErrorResponse(e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
        }
    }

    private Bundle generateResponse(int status) {
        Bundle response = new Bundle();
        response.putString("code", status == PrinterStatus.OK ? "SUCCESS" : "ERROR");
        response.putString("message", String.valueOf(status));
        return response;
    }

    private Bundle generateErrorResponse(String message) {
        Bundle response = new Bundle();
        response.putString("code", "ERROR");
        response.putString("message", message);
        return response;
    }

    private void validatePrintContent(List<Bundle> printableContent) {
        if (printableContent == null) {
            throw new IllegalArgumentException("Invalid print data: printable_content");
        }

        for (Bundle content : printableContent) {
            String type = content.getString("type");

            if ("text".equals(type)) {
                String contentOfType = content.getString("content");
                String align = content.getString("align");
                String size = content.getString("size");

                if (contentOfType == null) throw new IllegalArgumentException("Invalid printable_content data: content can't be null when type equals 'text'");
                if (!List.of("center", "right", "left").contains(align)) throw new IllegalArgumentException("Invalid printable_content data: align must be 'center', 'right', or 'left'");
                if (!List.of("big", "medium", "small").contains(size)) throw new IllegalArgumentException("Invalid printable_content data: size must be 'big', 'medium', or 'small'");
            }
        }
    }

    private void setValuePrint(List<Bundle> printableContent) throws RemoteException {
        for (Bundle content : printableContent) {
            String type = content.getString("type");

            switch (type) {
                case "text":
                    int formatSize;
                    switch (Objects.requireNonNull(content.getString("size"))) {
                        case "small":
                            formatSize = FontFormat.SMALL;
                            break;
                        case "medium":
                            formatSize = FontFormat.MEDIUM;
                            break;
                        case "big":
                            formatSize = FontFormat.LARGE;
                            break;
                        default:
                            formatSize = FontFormat.MEDIUM;
                            break;
                    }

                    int alignMode;
                    switch (content.getString("align")) {
                        case "left":
                            alignMode = AlignMode.LEFT;
                            break;
                        case "center":
                            alignMode = AlignMode.CENTER;
                            break;
                        case "right":
                            alignMode = AlignMode.RIGHT;
                            break;
                        default:
                            alignMode = AlignMode.LEFT;
                            break;
                    }

                    printer.defineFontFormat(formatSize);
                    printer.addText(alignMode, content.getString("content", "Conteúdo para imprimir null"));
                    break;

                case "image":
                    printer.addImageBitmap(AlignMode.CENTER, decodeBase64ToBitmap(content.getString("imagePath")));
                    break;

                default:
                    throw new IllegalArgumentException("Invalid printable_content data: Invalid type");
            }
        }

        printer.addText(AlignMode.LEFT, "\n\n");
    }


    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}

