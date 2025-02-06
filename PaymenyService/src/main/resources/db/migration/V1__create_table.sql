CREATE TABLE payment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL, -- Reference to the user who made the payment
    event_id UUID NOT NULL, -- Reference to the event
    amount NUMERIC(10, 2) NOT NULL, -- Payment amount with two decimal precision
    payment_status VARCHAR NOT NULL, -- e.g., COMPLETED, REFUNDED
    payment_date TIMESTAMP NOT NULL -- Date and time of the payment
);
