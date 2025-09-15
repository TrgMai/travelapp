import argparse
import pandas as pd
import matplotlib.pyplot as plt

REQUIRED_FIELDS = ['id_number', 'full_name', 'date_of_birth', 'gender', 'nationality']


def analyze(front_csv: str, back_csv: str, output_prefix: str):
    """Analyze front/back CCCD data and generate charts.

    Parameters
    ----------
    front_csv : str
        Path to CSV file containing front side data.
    back_csv : str
        Path to CSV file containing back side data.
    output_prefix : str
        Prefix for generated chart image filenames.
    """
    # Load CSVs
    front_df = pd.read_csv(front_csv)
    back_df = pd.read_csv(back_csv)

    # Filter rows with at least one of required fields filled
    valid_front = front_df[REQUIRED_FIELDS].notna().any(axis=1)
    front_df = front_df.loc[valid_front]

    # Count presence/absence per field
    field_stats = {}
    for field in REQUIRED_FIELDS:
        present = front_df[field].notna().sum()
        missing = front_df[field].isna().sum()
        field_stats[field] = {'present': present, 'missing': missing}

    # Plot front statistics
    labels = REQUIRED_FIELDS
    present_counts = [field_stats[f]['present'] for f in labels]
    missing_counts = [field_stats[f]['missing'] for f in labels]

    x = range(len(labels))
    plt.figure(figsize=(10, 5))
    plt.bar(x, present_counts, label='Có', color='tab:green')
    plt.bar(x, missing_counts, bottom=present_counts, label='Không', color='tab:red')
    plt.xticks(list(x), labels, rotation=45, ha='right')
    plt.ylabel('Số lượng')
    plt.title('Thống kê thông tin mặt trước CCCD')
    plt.legend()
    plt.tight_layout()
    plt.savefig(f"{output_prefix}_front.png")
    plt.close()

    # Back side: check MRZ presence using card_number
    merged = front_df[['card_number']].merge(
        back_df[['card_number', 'mrz_string']], on='card_number', how='left'
    )
    has_mrz = merged['mrz_string'].notna().sum()
    total = len(merged)
    missing_mrz = total - has_mrz
    pct = (has_mrz / total * 100) if total else 0

    # Plot back statistics
    plt.figure(figsize=(6, 6))
    plt.pie(
        [has_mrz, missing_mrz],
        labels=['Có MRZ', 'Không MRZ'],
        autopct='%1.1f%%',
        startangle=90,
    )
    plt.title('Thống kê MRZ mặt sau CCCD')
    plt.savefig(f"{output_prefix}_back.png")
    plt.close()

    return field_stats, pct


def main():
    parser = argparse.ArgumentParser(description='Generate CCCD statistics report.')
    parser.add_argument('--front', required=True, help='CSV file with front side data')
    parser.add_argument('--back', required=True, help='CSV file with back side data')
    parser.add_argument('--output', default='cccd_report', help='Output prefix for charts')
    args = parser.parse_args()

    field_stats, pct = analyze(args.front, args.back, args.output)

    print('Thống kê mặt trước:')
    for field, stat in field_stats.items():
        print(f"{field}: {stat['present']} có, {stat['missing']} không")
    print(f'Tỷ lệ MRZ mặt sau: {pct:.2f}%')


if __name__ == '__main__':
    main()
