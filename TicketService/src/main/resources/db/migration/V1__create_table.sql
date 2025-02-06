CREATE TABLE ticket (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL, -- Reference to the event
    user_id UUID NOT NULL, -- Reference to the user who booked the ticket
    booking_date TIMESTAMP NOT NULL,
    status VARCHAR NOT NULL, -- e.g., AVAILABLE, BOOKED
    price NUMERIC(10, 2) NOT NULL -- Price with two decimal precision
);
