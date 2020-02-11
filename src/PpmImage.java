import java.io.*;

import static java.lang.Math.ceil;

class PpmImage extends Image {
    PpmImage() {
        this.width = 0;
        this.height = 0;
        this.channels = 0;
        this.depth = 0;
        this.data = null;
    }

    PpmImage(int width, int height, int channels, int depth) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.depth = depth;
        this.data = new float[width * height * channels];
    }


    public float clamp(float x, float start, float end) {
        return Float.min(Float.max(x, start), end);
    }

    public String skipSpaces(String line) {
        int i = 0;
        char a = line.charAt(i);
        while (a == ' ' || a == '\t') {
            i++;
            a = line.charAt(i);
        }
        if (i > 0)
            line = line.substring(i);
        return line;
    }

    public void skipLine(InputStream is) {
        try {
            int tmp = 0;
            while (tmp != 10)
                tmp = is.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String nextLine(InputStream is) {
        StringBuilder tmpString = new StringBuilder();
        try {
            int tmp;
            tmp = is.read();
            if ((char) tmp == '#')
                skipLine(is);
            else if (tmp != 10)
                tmpString.append((char) tmp);
            while (tmp != 10) {
                tmp = is.read();
                if (tmp != 10)
                    tmpString.append((char) tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpString.toString();
    }

    public void ppmImport(String filename) {
        String header, line;
        int tmp;
        /* apertura del file */
        try {
            InputStream inputStream = new FileInputStream(filename);
            header = nextLine(inputStream);
            if (header == null) {
                System.out.println("Could not read from " + filename);
            } else {
                if (header.equals("P5") || header.equals("P5\n")) {
                    this.channels = 1;
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                    parseDimensions(line);
                } else if (header.equals("P6") || header.equals("P6\n")) {
                    this.channels = 3;
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                    parseDimensions(line);
                } else {
                    line = nextLine(inputStream);
                    line = skipSpaces(line);
                }
                line = nextLine(inputStream);
                line = skipSpaces(line);
                this.depth = Integer.parseInt(line);
                this.data = new float[this.width * this.height * this.channels];
                byte[] byteData = new byte[this.width * this.height * this.channels];
                inputStream.read(byteData);

                float scale = 1.0f / ((float) this.depth);
                for (int i = 0; i < byteData.length; i++) {
                    tmp = (byteData[i] < 0) ? byteData[i] + 256 : byteData[i]; //signed to unsigned
                    this.data[i] = (float) tmp * scale;
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not open file " + filename);
        }
    }

    public void ppmExport(String filename) {
        int width, height, channels, depth;
        /* apertura del file */
        try {
            width = this.width;
            height = this.height;
            depth = this.depth;
            channels = this.channels;
            OutputStream outputStream = new FileOutputStream(filename);
            String tmp = "P6\n" + width + " " + height + "\n" + depth + "\n";
            outputStream.write(tmp.getBytes());
            int x;
            byte[] writeData = new byte[width * height * channels];
            for (int i = 0; i < width * height * channels; i++) {
                x = (int) ceil(clamp(this.data[i], 0, 1) * depth);
                writeData[i] = (byte) x;
            }
            outputStream.write(writeData);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not open file" + filename);
        }
    }


    public void parseDimensions(String line) {
        line = skipSpaces(line);
        String[] dimensions = line.split("\\s+");
        this.width = Integer.parseInt(dimensions[0]);
        this.height = Integer.parseInt(dimensions[1]);
    }

}
