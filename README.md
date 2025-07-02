# AInance - AI Financial Advisor

A modern Android financial management app with AI-powered insights and goal tracking.

## Features

### 💰 Transaction Management
- **Modern Add Transaction Dialog**: Sleek, glassy UI with category dropdown
- **Income & Expense Tracking**: Automatic categorization and balance calculation
- **Transaction History**: Complete record of all financial activities

### 🎯 Goal Management
- **Smart Goal Creation**: Set financial goals with custom target dates
- **Goal Payments**: Make payments towards goals that deduct from main balance
- **Progress Tracking**: Visual progress bars and percentage completion
- **Goal Types**: Monthly, yearly, and custom goal periods

### 🤖 AI Financial Advisor
- **Smart Insights**: AI-powered financial advice based on your spending patterns
- **Markdown Support**: Rich text formatting for AI responses
- **Contextual Analysis**: AI considers your goals and transaction history
- **Multiple AI Providers**: Support for OpenAI and local AI endpoints

### ⚙️ Modern UI & UX
- **Flat Design**: Clean, minimalistic interface with modern color scheme
- **Glassy Dialogs**: Beautiful glass-morphism design elements
- **Responsive Layout**: Optimized for all screen sizes
- **Settings Management**: Comprehensive settings with data reset functionality

## Technical Features

### 🏗️ Architecture
- **MVVM Pattern**: Clean architecture with ViewModel and LiveData
- **Room Database**: Local data persistence for offline functionality
- **Repository Pattern**: Clean data layer abstraction

### 📱 Android Components
- **Navigation Component**: Smooth fragment navigation
- **Material Design**: Following Google's Material Design guidelines
- **RecyclerView**: Efficient list rendering for transactions and goals
- **Shared Preferences**: User settings and preferences management

### 🔧 Data Management
- **Goal Logic**: Goals start at 0 progress, payments deduct from main balance
- **Transaction Tracking**: Automatic balance calculation (income - expenses)
- **Data Reset**: Complete data clearing functionality in settings
- **Export Ready**: Data structure ready for import/export features

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 (API level 24) or higher
- Java 8 or higher

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/MaiAphrodite/Ainance.git
   ```

2. Open the project in Android Studio

3. Build and run the app on your device or emulator

### Configuration
- **AI Service**: Configure your AI endpoint and API key in Settings
- **Currency**: Set your preferred currency symbol
- **Categories**: Customize income and expense categories

## How It Works

### Goal Management
1. **Create Goals**: Set a target amount and date for your financial goals
2. **Make Payments**: Pay towards goals using the "Pay" button - this deducts from your main balance
3. **Track Progress**: Monitor your progress with visual indicators

### AI Advisor
1. **Context Analysis**: The AI considers your income, expenses, and goals
2. **Smart Advice**: Get personalized financial recommendations
3. **Dynamic Responses**: Scrollable, markdown-formatted AI responses

### Balance Management
- **Main Balance** = Total Income - Total Expenses - Goal Payments
- **Goal Progress** = Sum of all payments made to that specific goal
- **Independent Tracking**: Goals don't automatically affect main balance until payments are made

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Roadmap

- [ ] Data export/import functionality
- [ ] Advanced AI integration with more providers
- [ ] Spending analytics and charts
- [ ] Budget planning features
- [ ] Notification reminders for goals
- [ ] Dark mode support
- [ ] Multi-currency support

---

**AInance** - Your intelligent financial companion 📊✨
