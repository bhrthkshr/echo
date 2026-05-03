# Echo Notification Sync - Backend Design

## Tech Stack
- **Runtime:** Node.js (TypeScript)
- **Framework:** Express.js or NestJS
- **Database:** MongoDB
- **Caching/Queue:** Redis + BullMQ
- **Push Notifications:** Firebase Admin SDK

## Data Models (MongoDB)

### User
```typescript
{
  _id: ObjectId,
  email: string,
  googleId: string,
  createdAt: Date
}
```

### Device
```typescript
{
  _id: ObjectId,
  userId: ObjectId,
  deviceId: string, // Hardware ID
  fcmToken: string,
  name: string,
  platform: 'android',
  lastSeen: Date,
  isActive: boolean
}
```

### Notification
```typescript
{
  _id: ObjectId,
  userId: ObjectId,
  sourceDeviceId: string,
  appPackage: string,
  appName: string,
  title: string,
  message: string,
  timestamp: Date,
  hash: string, // For deduplication
  createdAt: { type: Date, expires: '7d' } // TTL index
}
```

## API Endpoints

### Auth
- `POST /auth/login`: Handles Google/Email OTP. Returns JWT.
- `POST /auth/register-device`: Registers/Updates FCM token.

### Notifications
- `POST /notifications`: Senders push here.
  - Logic: Check `hash` for duplicates within last 1 hour. Store in DB. Push to all other active devices for `userId`.
- `GET /notifications?since=timestamp`: For manual sync/pull.

### Devices
- `GET /devices`: List user's devices.
- `DELETE /devices/:id`: Remove/Deactivate device.

## Push Strategy
1. App sends notification to `/notifications`.
2. Backend validates JWT and schema.
3. Backend saves to MongoDB (TTL handles cleanup).
4. Backend identifies other devices for the user.
5. Backend adds "Send FCM" tasks to BullMQ.
6. Worker processes tasks, calls Firebase Admin SDK.
7. If failure, BullMQ retries (exponential backoff).

## Security
- All requests require `Authorization: Bearer <JWT>`.
- Rate limiting on `/notifications` to prevent spam.
- Payload size limit.
