import { Link } from "react-router-dom"
import { Separator } from "@/components/ui/separator"
import { env } from "@/lib/env"

const LAST_UPDATED = "October 8, 2026"
const CONTACT_EMAIL = "privacy@rentrix.app"

export default function TermsPage() {
  return (
    <article className="container mx-auto max-w-3xl px-4 py-12">
      <header className="mb-8 space-y-2">
        <h1 className="text-3xl font-bold tracking-tight">Terms of Service</h1>
        <p className="text-sm text-muted-foreground">Last updated: {LAST_UPDATED}</p>
      </header>

      <div className="max-w-none space-y-8 text-sm leading-relaxed text-muted-foreground">
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">1. Acceptance of Terms</h2>
          <p>
            By creating an account or using {env.VITE_APP_NAME} ("the Service"), you agree to these
            Terms of Service. If you do not agree, do not use the Service. You must be at least 18
            years old to use the Service.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">2. The Service</h2>
          <p>
            {env.VITE_APP_NAME} is a community platform for reviewing rental properties. Users can:
          </p>
          <ul className="list-disc space-y-1 pl-5">
            <li>Browse flats and read tenant-submitted reviews.</li>
            <li>Add flats to the platform, including places they have lived in.</li>
            <li>Submit reviews, ratings (1–10), and optional proof-of-living documents.</li>
            <li>Landlords may list flats they rent out.</li>
          </ul>
          <p>
            We are not a landlord, broker, or property manager. We do not verify ownership or rental
            terms of any flat listed. All reviews and listings are user-generated.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">3. Your Account</h2>
          <ul className="list-disc space-y-1 pl-5">
            <li>You must provide accurate information when creating an account.</li>
            <li>You are responsible for keeping your password confidential.</li>
            <li>You must not share your account or impersonate others.</li>
            <li>You must notify us immediately of any unauthorized use.</li>
          </ul>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">4. User Content</h2>
          <p>
            You retain ownership of content you submit (reviews, flat details, photographs, proof
            documents). By submitting content, you grant us a worldwide, non-exclusive, royalty-free
            license to host, display, and distribute it as part of the Service.
          </p>
          <p>You agree not to submit content that:</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>Is false, defamatory, or misleading.</li>
            <li>Infringes anyone's copyright, trademark, or privacy.</li>
            <li>Contains hate speech, harassment, or threats.</li>
            <li>Promotes illegal activity.</li>
            <li>Contains spam, advertisements, or referral links.</li>
            <li>Impersonates another person or entity.</li>
          </ul>
          <p>
            We may remove any content at our discretion, with or without notice. Repeated violations
            result in account termination.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">5. Reviews & Moderation</h2>
          <p>
            Reviews are moderated before publication. We approve reviews that appear genuine and
            reject those that appear false, abusive, or off-topic. Approval does not guarantee
            accuracy — we do not independently verify every review.
          </p>
          <p>
            Landlords and owners may not post reviews of their own properties. Reviews must reflect
            a genuine experience as a tenant.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">6. Proof-of-Living Documents</h2>
          <p>
            If you upload proof-of-living documents, you confirm you have the right to share them
            and that they relate to the flat being reviewed. Documents are stored privately,
            reviewed by moderators, and never shown publicly. Providing false documents may result
            in permanent ban.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">7. Acceptable Use</h2>
          <p>You agree not to:</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>Scrape, crawl, or automate access to the Service without permission.</li>
            <li>Attempt to bypass security, rate limits, or moderation.</li>
            <li>Reverse engineer or copy the Service.</li>
            <li>Upload malware or harmful code.</li>
            <li>Use the Service to harass, threaten, or defraud anyone.</li>
          </ul>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">8. Copyright & DMCA</h2>
          <p>
            If you believe content on the Service infringes your copyright, email{" "}
            <a
              href={`mailto:${CONTACT_EMAIL}`}
              className="text-foreground underline underline-offset-4 hover:text-primary"
            >
              {CONTACT_EMAIL}
            </a>{" "}
            with: (a) the infringing content URL; (b) your contact details; (c) a statement of
            ownership; (d) a statement made in good faith that the use is unauthorized. We will
            respond within 30 days.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">9. Disclaimers</h2>
          <p>
            The Service is provided "as is" without warranties of any kind. We do not guarantee that
            flat listings are accurate, that reviews reflect current conditions, or that the Service
            will be uninterrupted or error-free.
          </p>
          <p>
            <span className="font-medium text-foreground">
              Do not rely solely on {env.VITE_APP_NAME}
            </span>{" "}
            when making rental decisions. Always visit a property in person and verify details
            before signing any lease.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">10. Limitation of Liability</h2>
          <p>
            To the maximum extent permitted by law, {env.VITE_APP_NAME} is not liable for any
            indirect, incidental, special, or consequential damages arising from your use of the
            Service, including damages related to rental decisions, tenancy disputes, or reliance on
            user-generated content.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">11. Termination</h2>
          <p>
            You may delete your account at any time. We may suspend or terminate your access if you
            violate these Terms or use the Service in a way that harms others. Upon termination,
            your right to use the Service ends; Sections 4, 9, 10, and 13 survive.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">12. Changes to Terms</h2>
          <p>
            We may update these Terms. Material changes will be communicated by email or a prominent
            notice. Continued use after changes constitutes acceptance.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">13. Governing Law</h2>
          <p>
            These Terms are governed by the laws of India, without regard to conflict of law
            principles. Any dispute will be resolved in the courts of competent jurisdiction in
            India. Users in the EU and California retain any rights granted by their local laws that
            cannot be waived.
          </p>
        </section>
        <section className="space-y-3">
          <h2 className="text-lg font-semibold text-foreground">14. Contact</h2>
          <p>
            Questions about these Terms? Email{" "}
            <a
              href={`mailto:${CONTACT_EMAIL}`}
              className="text-foreground underline underline-offset-4 hover:text-primary"
            >
              {CONTACT_EMAIL}
            </a>
            .
          </p>
        </section>
        <Separator className="my-8" />
        <p className="text-xs">
          See also our{" "}
          <Link
            to="/privacy"
            className="text-foreground underline underline-offset-4 hover:text-primary"
          >
            Privacy Policy
          </Link>
          .
        </p>
      </div>
    </article>
  )
}