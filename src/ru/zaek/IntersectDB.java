package ru.zaek;

public class IntersectDB {
    /**
     * An efficient structure for store intersection count of two positive
     *  integers
     * An intersection is a same value for two pairs so instead of matrix
     *  you have a triangle (about two times less memory usage).
     * For pair X > Y we store intersection a=X and b=Y, for pair Y > X
     *  we store a=Y and b=X.
     *
     * Starts from 1 and filling up to size
     *
     */
    public short[] data;
    public short[] positions;
    protected short size;

    public IntersectDB(short size) {
        initData(size);
    }
    public IntersectDB() {}

    protected void initData(short size) {
        this.size = size;
        /*
         * How do we calculate the number of entities?
         * * 1 2 3 4 5
         * 1 * * * * *
         * 2 - * * * *
         * 3 - - * * *
         * 4 - - - * *
         * 5 - - - - *
         *
         * ->
         * 0     5    9   12 14
         * ***** **** *** ** *
         *
         * * 1 2 3 4
         * 1 * * * *
         * 2 - * * *
         * 3 - - * *
         * 4 - - - *
         * 0 4 7 9
         *
         *
         * It is a half part of square matrix NxN plus half part of numbers in diagonal
         * (n * n) / 2 + n / 2 -> n * n/2 + n/2 -> n/2 * (n+1)
         *
         *  The formula is [(n+1)*n/2]
         */
        data = new short[(size + 1) * size / 2];
        positions = new short[size];

        positions[0] = 0;

        short len = size;
        for(short i =1; i < size; i++) {
            positions[i] = (short) ((len--) + positions[i-1]);
        }
    }

    public void set(short x, short y, short value) {
        data[getPosition(x, y)] = value;
    }

    public void inc(short x, short y) {
        data[getPosition(x, y)]++;
    }

    public short get(short x, short y) {
        return data[getPosition(x, y)];
    }

    private short getPosition(short x, short y) {
        if(x < y) {
            // swap
            short temp = x;
            x = y;
            y = temp;
        }

        if(x > positions.length) {
            throw new IndexOutOfBoundsException();
        }

        return (short) (positions[x-1] + (y-x));
    }

    public void print() {
        for(int i = 0; i < data.length;i++) {
            System.out.printf("%2d %3d\n", i, data[i]);
        }
    }
}
