-- Initialize database with some basic setup
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_assets_ticker ON assets(ticker);
CREATE INDEX IF NOT EXISTS idx_assets_active ON assets(active);
CREATE INDEX IF NOT EXISTS idx_assets_category ON assets(category);

CREATE INDEX IF NOT EXISTS idx_price_history_ticker_timestamp ON price_history(ticker, timestamp);
CREATE INDEX IF NOT EXISTS idx_price_history_timestamp ON price_history(timestamp);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_ticker ON transactions(ticker);
CREATE INDEX IF NOT EXISTS idx_transactions_timestamp ON transactions(timestamp);

-- Create materialized view for performance
CREATE MATERIALIZED VIEW IF NOT EXISTS daily_price_summary AS
SELECT 
    ticker,
    DATE(timestamp) as date,
    MIN(low) as daily_low,
    MAX(high) as daily_high,
    FIRST_VALUE(open ORDER BY timestamp) as daily_open,
    LAST_VALUE(close ORDER BY timestamp) as daily_close,
    SUM(volume) as daily_volume,
    COUNT(*) as records_count
FROM price_history 
GROUP BY ticker, DATE(timestamp)
ORDER BY ticker, date DESC;

-- Create unique index on materialized view
CREATE UNIQUE INDEX IF NOT EXISTS idx_daily_summary_ticker_date 
ON daily_price_summary(ticker, date);

-- Function to refresh materialized view
CREATE OR REPLACE FUNCTION refresh_daily_summary()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY daily_price_summary;
END;
$$ LANGUAGE plpgsql;