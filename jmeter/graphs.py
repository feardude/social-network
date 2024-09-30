import csv

import matplotlib.pyplot as plt
import numpy as np

files = [
    # 'result-no-index-1.csv',
    # 'result-no-index-10.csv',
    # 'result-no-index-100.csv',
    # 'result-no-index-1000.csv',
    # 'result-index-last_first_name_idx-1.csv',
    # 'result-index-last_first_name_idx-10.csv',
    # 'result-index-last_first_name_idx-100.csv',
    # 'result-index-last_first_name_idx-1000.csv',
    # 'result-index-1.csv',
    # 'result-index-10.csv',
    'result-index-100.csv',
    'result-index-1000.csv',
]


def csv_to_graph(name):
    x = []
    y = []
    with open(name, 'r') as f:
        csv_reader = csv.reader(f, delimiter=',')
        next(csv_reader, None)

        prev_timestamp = 0
        tot_bytes = 0
        x_i = 0

        for rows in csv_reader:
            if x_i >= 60:
                break

            curr_timestamp = int(rows[0])
            cur_bytes = int(rows[9])

            if (curr_timestamp - prev_timestamp) > 1000:
                x.append(x_i)
                y.append(tot_bytes / 1000 / 1000)

                prev_timestamp = curr_timestamp
                x_i += 1
                tot_bytes = 0

            tot_bytes += cur_bytes

        print(f'{x=}')
        print(f'{y=}')
        plt.plot(x, y)
        plt.title('Throughput')
        plt.xlabel('seconds')
        plt.ylabel('MBytes')
        plt.xticks(np.arange(0, 61, 5))

        plot_file_name = name.replace('.csv', '.png')
        plot_file_name = 'throughput-' + plot_file_name
        plt.savefig(plot_file_name, dpi=300)
        plt.show()


for filename in files:
    print(filename)
    csv_to_graph(filename)
    print()
